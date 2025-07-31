package org.scoula.member.service;

import org.scoula.member.dto.RegisterDTO;
import org.scoula.member.dto.SocialRegisterDTO;
import org.scoula.security.account.dto.AuthResultDTO;

public interface SignupService {
    boolean isValidPassword(String password);
    void updateProfile(Long memberId, String nickname, Boolean receivePushNotification);
    void register(RegisterDTO dto, String phoneNumber);
    void socialRegister(SocialRegisterDTO dto);
    AuthResultDTO createAuthResult(String email);
}
