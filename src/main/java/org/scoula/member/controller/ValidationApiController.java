package org.scoula.member.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.scoula.member.service.MemberService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "중복 확인 API" , description = "회원가입 시 이메일과 닉네임의 중복 여부를 확인하는 API.")
@RestController
@RequestMapping("/api/validation/check")
@RequiredArgsConstructor
public class ValidationApiController {
    private final MemberService memberService;

    @ApiOperation(
            value = "이메일 중복 확인",
            notes = "회원가입 시 입력한 이메일이 이미 사용 중인지 확인합니다."
    )
    @GetMapping("/email")
    public ApiResponse<Boolean> checkEmail(@RequestParam String email) {
        boolean available = memberService.isEmailAvailable(email);
        if (available) {
            return ApiResponse.success(ResponseCode.VALID_EMAIL,true);
        } else {
            return ApiResponse.fail(ResponseCode.DUPLICATED_EMAIL,false);
        }
    }

    @ApiOperation(
            value = "닉네임 중복 확인",
            notes = "회원가입 시 입력한 닉네임이 이미 사용 중인지 확인합니다."
    )
    @GetMapping("/nickname")
    public ApiResponse<Boolean> checkNickname(@RequestParam String nickname) {
        boolean available = memberService.isNicknameAvailable(nickname);
        if (available) {
            return ApiResponse.success(ResponseCode.VALID_NICKNAME, true);
        } else {
            return ApiResponse.fail(ResponseCode.DUPLICATED_NICKNAME, false);
        }
    }
}