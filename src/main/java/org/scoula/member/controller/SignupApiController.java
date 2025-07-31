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

    @ApiOperation(
            value = "회원가입",
            notes = "회원가입을 처리하고 성공 시 회원 정보를 반환합니다."
    )
    @PostMapping("/signup")
    public ApiResponse<?> register(@Validated @RequestBody RegisterDTO dto) {
        try {
            signupService.register(dto, dto.getPhoneNumber());
            return ApiResponse.success(ResponseCode.SIGNUP_SUCCESS);
        } catch (Exception e) {
            log.error("회원가입 오류", e);
            return ApiResponse.fail(ResponseCode.SERVER_ERROR);
        }
    }

    @ApiOperation(
            value = "소셜 로그인 회원가입",
            notes = "소셜 로그인을 통한 회원가입을 처리합니다."
    )
    @PostMapping("/signup/social")
    public ApiResponse<?> socialRegister(@Validated @RequestBody SocialRegisterDTO dto) {
        try {
            log.info("[DEBUG] 소셜 회원가입 요청 - 이메일: {}, 닉네임: {}", dto.getEmail(), dto.getNickname());
            signupService.socialRegister(dto);
            AuthResultDTO authResult = signupService.createAuthResult(dto.getEmail());
            return ApiResponse.success(ResponseCode.SIGNUP_SUCCESS, authResult);

        } catch (Exception e) {
            return ApiResponse.fail(ResponseCode.SERVER_ERROR, e.getMessage());
        }
    }
}