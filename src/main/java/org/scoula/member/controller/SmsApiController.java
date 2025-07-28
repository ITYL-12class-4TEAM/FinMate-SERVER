package org.scoula.member.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.scoula.member.service.SmsService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@Api(tags = "사용자 인증 API")
@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class SmsApiController {

    private final SmsService smsService;

    @ApiOperation(value = "인증번호 문자 전송", notes = "휴대폰 번호로 인증번호 문자를 전송합니다.")
    @GetMapping("/send-verification")
    public ApiResponse<?> sendSms(@RequestParam String phoneNumber) {
        smsService.certificateSMS(phoneNumber);
        return ApiResponse.success(ResponseCode.SMS_SEND_SUCCESS);
    }

    @ApiOperation(value = "인증번호 검증", notes = "입력한 인증번호가 맞는지 검증합니다.")
    @PostMapping("/verify-code")
    public ApiResponse<?> verifySms(@RequestParam String phoneNumber, @RequestParam String code) {

        boolean verified = smsService.verifyCode(phoneNumber, code);
        if (verified) {
            smsService.markPhoneAsVerified(phoneNumber);
            return ApiResponse.success(ResponseCode.SMS_VERIFY_SUCCESS);
        } else {
            return ApiResponse.fail(ResponseCode.SMS_VERIFY_FAILED);
        }
    }


}