package org.scoula.member.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.scoula.common.dto.ResponseDTO;
import org.scoula.common.service.RedisService;
import org.scoula.member.service.SmsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Api(tags = "사용자 인증 API")
@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class SmsApiController {

    private final SmsService smsService;

    @GetMapping("/send-verification")
    public ResponseEntity<ResponseDTO<Void>> sendSms(@RequestParam String phoneNumber) {
        smsService.certificateSMS(phoneNumber);
        return ResponseEntity.ok(new ResponseDTO<>(true, "인증번호 전송 완료", null));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<ResponseDTO<Void>> verifySms(@RequestParam String phoneNumber, @RequestParam String code) {

        boolean verified = smsService.verifyCode(phoneNumber, code);
        if (verified) {
            smsService.markPhoneAsVerified(phoneNumber);
            return ResponseEntity.ok(new ResponseDTO<>(true, "인증 성공", null));
        } else {
            return ResponseEntity.badRequest().body(new ResponseDTO<>(false, "인증번호가 일치하지 않거나 만료되었습니다.", null));
        }
    }


}