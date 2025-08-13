package org.scoula.auth.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

import java.security.Principal;

@Api(tags = "아이디 비밀번호 찾기 및 토큰 관련 API", description = "회원 인증 및 계정 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthApiController {

    private final AuthService authService;
    private final JwtProcessor jwtProcessor;

    @ApiOperation(
            value = "토큰 갱신",
            notes = "리프레시 토큰으로 새로운 액세스 토큰을 발급합니다."
    )
    @PostMapping("/refresh")
    public ApiResponse<TokenResponseDTO> refresh(
            @ApiParam(value = "리프레시 토큰", required = true)
            @RequestBody RefreshRequest request
    ) {
        TokenResponseDTO response = authService.refreshToken(request.getRefreshToken());
        return ApiResponse.success(ResponseCode.TOKEN_REFRESH_SUCCESS, response);
    }

    @ApiOperation(
            value = "아이디 찾기",
            notes = "이름과 휴대폰 번호로 아이디를 조회합니다."
    )
    @PostMapping("/find-id")
    public ApiResponse<FindIdResponseDTO> findId(
            @ApiParam(value = "아이디 찾기 요청 정보", required = true)
            @RequestBody FindIdRequest request
    ) {
        FindIdResponseDTO response = authService.findUsernameByNameAndPhone(request);
        return ApiResponse.success(ResponseCode.ID_FIND_SUCCESS, response);
    }

    @ApiOperation(
            value = "비밀번호 찾기",
            notes = "이름과 휴대폰 번호로 비밀번호를 조회합니다."
    )
    @PostMapping("/find-password")
    public ApiResponse<FindIdResponseDTO> findPassword(
            @ApiParam(value = "비밀번호 찾기 요청 정보", required = true)
            @RequestBody FindIdRequest request
    ) {
        FindIdResponseDTO response = authService.findPassword(request);
        return ApiResponse.success(ResponseCode.PASSWORD_FIND_SUCCESS, response);
    }

    @ApiOperation(
            value = "비밀번호 재설정",
            notes = "비밀번호를 재설정합니다."
    )
    @PostMapping("/reset-password")
    public ApiResponse<?> resetPassword(
            @ApiParam(value = "비밀번호 재설정 요청 정보", required = true)
            @RequestBody ResetPasswordRequest request
    ) {
        authService.resetPassword(request);
        return ApiResponse.success(ResponseCode.PASSWORD_RESET_SUCCESS);
    }

    @ApiOperation(
            value = "회원 프로필 수정",
            notes = "회원 프로필 정보를 수정합니다."
    )
    @PutMapping("/profile")
    public ApiResponse<?> updateProfile(
            @ApiParam(value = "프로필 수정 요청 정보", required = true)
            @RequestBody UpdateProfileRequest request
    ) {
        authService.updateProfile(request);
        return ApiResponse.success(ResponseCode.PROFILE_UPDATE_SUCCESS);
    }

    @ApiOperation(
            value = "회원탈퇴",
            notes = "회원 탈퇴를 처리합니다."
    )
    @DeleteMapping("/withdraw")
    public ApiResponse<?> withdrawMember(
            @ApiParam(value = "회원탈퇴 요청 정보", required = true)
            @RequestBody WithdrawRequest request,
            Principal principal
    ) {
        String email = principal.getName();
        authService.withdrawMember(request, email);
        return ApiResponse.success(ResponseCode.MEMBER_WITHDRAW_SUCCESS);
    }

    @ApiOperation(
            value = "비밀번호 확인",
            notes = "비밀번호가 일치하는지 확인합니다."
    )
    @PostMapping("/check-password")
    public ApiResponse<?> checkPassword(
            @ApiParam(value = "비밀번호 확인 요청 정보", required = true)
            @RequestBody PasswordCheckRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        String email = jwtProcessor.getUsername(token);

        authService.checkPassword(request, email);
        return ApiResponse.success(ResponseCode.PASSWORD_CHECK_SUCCESS);
    }

    @ApiOperation(
            value = "OAuth2 임시 코드로 토큰 교환",
            notes = "OAuth2 임시 코드로 토큰을 발급받습니다."
    )
    @PostMapping("/oauth2/token")
    public ApiResponse<?> exchangeToken(
            @ApiParam(value = "OAuth2 임시 코드", required = true)
            @RequestParam String code
    ) {
        AuthResultDTO result = authService.exchangeToken(code);
        return ApiResponse.success(ResponseCode.LOGIN_SUCCESS, result);
    }
}