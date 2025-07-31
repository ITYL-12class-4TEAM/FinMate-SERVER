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

        try {
            OAuth2User oauth2User = super.loadUser(userRequest);
            CustomUser result = processOAuth2User(userRequest, oauth2User);
            return result;

        } catch (Exception ex) {
            throw new OAuth2AuthenticationException("OAuth2 사용자 처리 중 오류가 발생했습니다.");
        }
    }

    private CustomUser processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oauth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId, oauth2User.getAttributes());

        if (oauth2UserInfo.getEmail() == null || oauth2UserInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationException("OAuth2 제공자로부터 이메일을 찾을 수 없습니다.");
        }
        MemberVO member = memberMapper.selectByEmail(oauth2UserInfo.getEmail());

        if (member != null) {
            if (!registrationId.equals(member.getSocialType())) {
                throw new OAuth2AuthenticationException(
                        "이미 " + member.getSocialType() + "로 가입된 이메일입니다.");
            }
            member = updateExistingUser(member, oauth2UserInfo);
        } else {
            member = registerNewUser(registrationId, oauth2UserInfo);
        }

        return new CustomUser(member, oauth2User.getAttributes());
    }
    private MemberVO registerNewUser(String registrationId, OAuth2UserInfo oauth2UserInfo) {
        MemberVO newMember = MemberVO.builder()
                .email(oauth2UserInfo.getEmail())
                .username(oauth2UserInfo.getName())
                .profileImage(oauth2UserInfo.getImageUrl())
                .socialType(registrationId)
                .socialId(oauth2UserInfo.getId())
                .role("USER")
                .level(1)
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
                .nickname(existingMember.getNickname())
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
