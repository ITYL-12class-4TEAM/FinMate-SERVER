package org.scoula.auth.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.scoula.auth.dto.request.*;
import org.scoula.auth.dto.FindIdResponseDTO;
import org.scoula.auth.dto.TokenResponseDTO;
import org.scoula.auth.service.AuthService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.scoula.security.util.JwtProcessor;
import org.springframework.web.bind.annotation.*;
import org.scoula.security.account.dto.AuthResultDTO;

@Api(tags = "아이디 비밀번호 찾기 및 토큰 갱신 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthApiController {

    private final AuthService authService;
    private final JwtProcessor jwtProcessor;


    @ApiOperation("리프레시 토큰으로 새로운 액세스 토큰 발급")
    @PostMapping("/refresh")
    public ApiResponse<TokenResponseDTO> refresh(@RequestBody RefreshRequest request) {
        TokenResponseDTO response = authService.refreshToken(request.getRefreshToken());
        return ApiResponse.success(ResponseCode.TOKEN_REFRESH_SUCCESS, response);
    }

    @ApiOperation("아이디 찾기")
    @PostMapping("/find-id")
    public ApiResponse<FindIdResponseDTO> findId(@RequestBody FindIdRequest request) {
        FindIdResponseDTO response = authService.findUsernameByNameAndPhone(request);
        return ApiResponse.success(ResponseCode.ID_FIND_SUCCESS, response);
    }

    @ApiOperation("비밀번호 찾기")
    @PostMapping("/find-password")
    public ApiResponse<FindIdResponseDTO> findPassword(@RequestBody FindIdRequest request) {
        FindIdResponseDTO response = authService.findPassword(request);
        return ApiResponse.success(ResponseCode.PASSWORD_FIND_SUCCESS, response);
    }

    @ApiOperation("비밀번호 재설정")
    @PostMapping("/reset-password")
    public ApiResponse<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.success(ResponseCode.PASSWORD_RESET_SUCCESS);
    }

    @ApiOperation("회원 프로필 수정")
    @PutMapping("/profile")
    public ApiResponse<?> updateProfile(@RequestBody UpdateProfileRequest request) {
        authService.updateProfile(request);
        return ApiResponse.success(ResponseCode.PROFILE_UPDATE_SUCCESS);
    }

    @ApiOperation("회원탈퇴")
    @DeleteMapping("/withdraw")
    public ApiResponse<?> withdrawMember(@RequestBody WithdrawRequest request) {
        authService.withdrawMember(request);
        return ApiResponse.success(ResponseCode.MEMBER_WITHDRAW_SUCCESS);
    }

    @ApiOperation("비밀번호 확인")
    @PostMapping("/check-password")
    public ApiResponse<?> checkPassword(@RequestBody PasswordCheckRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtProcessor.getUsername(token);

        authService.checkPassword(request, email);
        return ApiResponse.success(ResponseCode.PASSWORD_CHECK_SUCCESS);
    }
    @ApiOperation("OAuth2 임시 코드로 토큰 교환")
    @PostMapping("/oauth2/token")
    public ApiResponse<?> exchangeToken(@RequestParam String code) {
         AuthResultDTO result = authService.exchangeToken(code);
         return ApiResponse.success(ResponseCode.LOGIN_SUCCESS,result);

    }
}
