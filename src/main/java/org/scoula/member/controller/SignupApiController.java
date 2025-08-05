package org.scoula.member.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.member.dto.RegisterDTO;
import org.scoula.member.dto.SocialRegisterDTO;
import org.scoula.member.service.SignupService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.scoula.security.account.dto.AuthResultDTO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Log4j2
@Api(tags = "회원가입 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SignupApiController {
    private final SignupService signupService;

    @ApiOperation(value = "회원가입")
    @PostMapping("/signup")
    public ApiResponse<?> register(@Validated @RequestBody RegisterDTO dto) {
        signupService.register(dto, dto.getPhoneNumber());
        return ApiResponse.success(ResponseCode.SIGNUP_SUCCESS);
    }

    @ApiOperation(value = "소셜 로그인 회원가입")
    @PostMapping("/signup/social")
    public ApiResponse<AuthResultDTO> socialRegister(@Validated @RequestBody SocialRegisterDTO dto) {
        signupService.socialRegister(dto);
        AuthResultDTO authResult = signupService.createAuthResult(dto.getEmail());
        return ApiResponse.success(ResponseCode.SIGNUP_SUCCESS, authResult);
    }
}