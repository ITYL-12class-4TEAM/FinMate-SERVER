package org.scoula.auth.service;

import org.scoula.auth.dto.*;
import org.scoula.auth.dto.request.*;
import org.scoula.security.account.dto.AuthResultDTO;

public interface AuthService {
    TokenResponseDTO refreshToken(String refreshToken);
    FindIdResponseDTO findUsernameByNameAndPhone(FindIdRequest request);
    FindIdResponseDTO findPassword(FindIdRequest request);
    void resetPassword(ResetPasswordRequest request);
    void updateProfile(UpdateProfileRequest request);
    void withdrawMember(WithdrawRequest request , String email);
    void checkPassword(PasswordCheckRequest request, String email);
    AuthResultDTO exchangeToken(String code);
}
