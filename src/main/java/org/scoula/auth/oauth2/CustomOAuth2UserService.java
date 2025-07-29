package org.scoula.auth.oauth2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.security.account.domain.CustomUser;
import org.scoula.security.account.domain.MemberVO;
import org.scoula.member.mapper.MemberMapper;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberMapper memberMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("[DEBUG] ===== CustomOAuth2UserService.loadUser 시작 =====");
        log.info("[DEBUG] 클라이언트 등록 ID: {}", userRequest.getClientRegistration().getRegistrationId());
        log.info("[DEBUG] 요청 정보: {}", userRequest);

        try {
            OAuth2User oauth2User = super.loadUser(userRequest);
            log.info("[DEBUG] OAuth2User 로드 성공");
            log.info("[DEBUG] OAuth2User 속성: {}", oauth2User.getAttributes());

            CustomUser result = processOAuth2User(userRequest, oauth2User);
            log.info("[DEBUG] CustomUser 생성 완료 - 회원ID: {}", result.getMember().getMemberId());
            log.info("[DEBUG] 신규 회원 여부: {}", result.getMember().getIsNewMember());
            log.info("[DEBUG] ===== CustomOAuth2UserService.loadUser 종료 =====");
            return result;

        } catch (Exception ex) {
            log.error("[DEBUG] OAuth2 사용자 처리 중 오류 발생", ex);
            throw new OAuth2AuthenticationException("OAuth2 사용자 처리 중 오류가 발생했습니다.");
        }
    }

    private CustomUser processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        log.info("[DEBUG] processOAuth2User 시작");

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("[DEBUG] 등록 ID: {}", registrationId);

        OAuth2UserInfo oauth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId, oauth2User.getAttributes());
        log.info("[DEBUG] OAuth2UserInfo 생성 완료 - 이메일: {}", oauth2UserInfo.getEmail());

        if (oauth2UserInfo.getEmail() == null || oauth2UserInfo.getEmail().isEmpty()) {
            log.error("[DEBUG] 이메일 정보 없음");
            throw new OAuth2AuthenticationException("OAuth2 제공자로부터 이메일을 찾을 수 없습니다.");
        }

        log.info("[DEBUG] 기존 회원 조회 시작");
        MemberVO member = memberMapper.selectByEmail(oauth2UserInfo.getEmail());

        if (member != null) {
            log.info("[DEBUG] 기존 회원 발견 - ID: {}", member.getMemberId());

            if (!registrationId.equals(member.getSocialType())) {
                log.error("[DEBUG] 소셜 타입 불일치 - 기존: {}, 현재: {}", member.getSocialType(), registrationId);
                throw new OAuth2AuthenticationException(
                        "이미 " + member.getSocialType() + "로 가입된 이메일입니다.");
            }
            member = updateExistingUser(member, oauth2UserInfo);
        } else {
            log.info("[DEBUG] 신규 회원 등록 시작");
            member = registerNewUser(registrationId, oauth2UserInfo);
        }

        log.info("[DEBUG] CustomUser 생성 - 회원ID: {}", member.getMemberId());
        return new CustomUser(member, oauth2User.getAttributes());
    }
    private MemberVO registerNewUser(String registrationId, OAuth2UserInfo oauth2UserInfo) {
        MemberVO newMember = MemberVO.builder()
                .email(oauth2UserInfo.getEmail())
                .nickname(oauth2UserInfo.getName())
                .username(oauth2UserInfo.getName())
                .profileImage(oauth2UserInfo.getImageUrl())
                .socialType(registrationId)
                .socialId(oauth2UserInfo.getId())
                .role("USER")
                .level(1)
                .status("ACTIVE")
                .isNewMember(true)  // 신규 회원 표시
                .createdAt(new Date())
                .build();

        memberMapper.insertSocialMember(newMember);
        return newMember;
    }

    private MemberVO updateExistingUser(MemberVO existingMember, OAuth2UserInfo oauth2UserInfo) {
        MemberVO updatedMember = MemberVO.builder()
                .memberId(existingMember.getMemberId())
                .email(oauth2UserInfo.getEmail())
                .nickname(oauth2UserInfo.getName())
                .username(existingMember.getUsername())
                .profileImage(oauth2UserInfo.getImageUrl())
                .socialType(existingMember.getSocialType())
                .socialId(oauth2UserInfo.getId())
                .isNewMember(false)  // 기존 회원
                .phoneNumber(existingMember.getPhoneNumber())
                .birthDate(existingMember.getBirthDate())
                .gender(existingMember.getGender())
                .build();

        memberMapper.updateSocialMember(updatedMember);
        return updatedMember;
    }
}
