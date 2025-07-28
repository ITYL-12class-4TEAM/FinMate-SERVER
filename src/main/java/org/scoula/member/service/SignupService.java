package org.scoula.member.service;

import org.scoula.member.dto.RegisterDTO;

public interface SignupService {
    boolean isValidPassword(String password);
    void updateProfile(Long memberId, String nickname, Boolean receivePushNotification);
    void register(RegisterDTO dto, String phoneNumber);
}
