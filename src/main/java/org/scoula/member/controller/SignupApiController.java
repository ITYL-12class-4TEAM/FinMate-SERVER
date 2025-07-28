package org.scoula.member.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.scoula.member.dto.RegisterDTO;
import org.scoula.member.service.SignupService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
        signupService.register(dto, dto.getPhoneNumber());
        return ApiResponse.success(ResponseCode.SIGNUP_SUCCESS);
    }
}