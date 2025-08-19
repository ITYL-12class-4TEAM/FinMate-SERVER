package org.scoula.auth.oauth2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.response.ResponseCode;
import org.scoula.security.account.domain.CustomUser;
import org.scoula.security.account.domain.MemberVO;
import org.scoula.member.mapper.MemberMapper;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
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
        OAuth2User oauth2User = super.loadUser(userRequest);
        return processOAuth2User(userRequest, oauth2User);
    }

    private CustomUser processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oauth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId, oauth2User.getAttributes());

        if (oauth2UserInfo.getEmail() == null || oauth2UserInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error(
                            ResponseCode.OAUTH2_EMAIL_NOT_PROVIDED.name(),
                            ResponseCode.OAUTH2_EMAIL_NOT_PROVIDED.getMessage(),
                            null
                    )
            );
        }

        // 1. 먼저 활성 회원 찾기
        MemberVO member = memberMapper.selectByEmail(oauth2UserInfo.getEmail());

        if (member != null) {
            // 활성 회원 처리
            if ("none".equals(member.getSocialType())) {
                throw new OAuth2AuthenticationException(
                        new OAuth2Error(
                                ResponseCode.EMAIL_ALREADY_REGISTERED.name(),
                                ResponseCode.EMAIL_ALREADY_REGISTERED.getMessage(),
                                null
                        )
                );
            }

            if (!registrationId.equals(member.getSocialType())) {
                throw new OAuth2AuthenticationException(
                        new OAuth2Error(
                                ResponseCode.OAUTH2_DIFFERENT_SOCIAL_TYPE.name(),
                                ResponseCode.OAUTH2_DIFFERENT_SOCIAL_TYPE.getMessage(),
                                null
                        )
                );
            }

            member = updateExistingUser(member, oauth2UserInfo);
        } else {
            // 2. 탈퇴한 회원 포함해서 찾기
            MemberVO deletedMember = memberMapper.findByEmailIncludeDeleted(oauth2UserInfo.getEmail());

            if (deletedMember != null && "DELETED".equals(deletedMember.getStatus())) {
                // 탈퇴한 회원 재활성화
                member = reactivateUser(deletedMember, registrationId, oauth2UserInfo);
            } else {
                // 3. 완전히 새로운 회원 등록
                member = registerNewUser(registrationId, oauth2UserInfo);
            }
        }

        return new CustomUser(member, oauth2User.getAttributes());
    }

    private MemberVO reactivateUser(MemberVO deletedMember, String registrationId, OAuth2UserInfo oauth2UserInfo) {
        MemberVO reactivatedMember = MemberVO.builder()
                .memberId(deletedMember.getMemberId())
                .email(oauth2UserInfo.getEmail())
                .username(oauth2UserInfo.getName())
                .profileImage(oauth2UserInfo.getImageUrl())
                .socialType(registrationId)
                .socialId(oauth2UserInfo.getId())
                .role("USER")
                .level(1)
                .isNewMember(true) // 재가입이므로 새 회원으로 처리
                .updatedAt(new Date())
                .build();

        memberMapper.reactivateMember(reactivatedMember);
        return reactivatedMember;
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
                .isNewMember(true)
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
                .isNewMember(false)
                .birthDate(existingMember.getBirthDate())
                .gender(existingMember.getGender())
                .receivePushNotification(existingMember.getReceivePushNotification())
                .build();

        memberMapper.updateSocialMember(updatedMember);
        return updatedMember;
    }
}