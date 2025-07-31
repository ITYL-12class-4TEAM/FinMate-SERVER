package org.scoula.auth.service;

import org.scoula.auth.dto.*;
import org.scoula.auth.dto.request.*;

public interface AuthService {
    TokenResponseDTO refreshToken(String refreshToken);
    FindIdResponseDTO findUsernameByNameAndPhone(FindIdRequest request);
    FindIdResponseDTO findPassword(FindIdRequest request);
    void resetPassword(ResetPasswordRequest request);
    void updateProfile(UpdateProfileRequest request);
    void withdrawMember(WithdrawRequest request);
    void checkPassword(PasswordCheckRequest request, String email);
}
