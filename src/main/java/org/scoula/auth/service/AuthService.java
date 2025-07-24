package org.scoula.auth.service;

import org.scoula.auth.dto.FindIdRequest;
import org.scoula.auth.dto.FindIdResponseDTO;
import org.scoula.auth.dto.ResetPasswordRequest;
import org.scoula.auth.dto.TokenResponseDTO;
import org.scoula.auth.dto.UpdateProfileRequest;

public interface AuthService {
    TokenResponseDTO refreshToken(String refreshToken);
    FindIdResponseDTO findUsernameByNameAndPhone(FindIdRequest request);
    FindIdResponseDTO findPassword(FindIdRequest request);
    void resetPassword(ResetPasswordRequest request);
    void updateProfile(UpdateProfileRequest request);
}
