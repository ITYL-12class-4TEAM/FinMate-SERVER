package org.scoula.auth.service;

import org.scoula.auth.dto.*;

public interface AuthService {
    TokenResponseDTO refreshToken(String refreshToken);
    FindIdResponseDTO findUsernameByNameAndPhone(FindIdRequest request);
    FindIdResponseDTO findPassword(FindIdRequest request);
    void resetPassword(ResetPasswordRequest request);
    void updateProfile(UpdateProfileRequest request);
    void withdrawMember(WithdrawRequest request);
}
