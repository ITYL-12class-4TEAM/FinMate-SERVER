package org.scoula.member.service;

import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.scoula.common.service.RedisService;
import org.scoula.member.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class SmsService {

    private final RedisService redisService;

    @Value("${coolsms.apiKey}")
    private String apiKey;

    @Value("${coolsms.secretkey}")
    private String apiSecret;

    private final String fromNumber = "01072730152";

    private String createRandomNumber() {
        Random rand = new Random();
        StringBuilder randomNum = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            randomNum.append(rand.nextInt(10));
        }
        return randomNum.toString();
    }

    public boolean verifyCode(String phoneNumber, String code) {
        String savedCode = redisService.get(phoneNumber);
        if (savedCode == null) return false;
        if (savedCode.equals(code)) {
            redisService.delete(phoneNumber);
            return true;
        }
        return false;
    }
    public void markPhoneAsVerified(String phoneNumber) {
        String verificationKey = "phone_verified:" + phoneNumber;
        redisService.save(verificationKey, "true", 30); // 30분 유효
    }
    @Transactional
    public void certificateSMS(String phoneNumber) {
        // DefaultMessageService로 생성
        DefaultMessageService messageService = new DefaultMessageService(apiKey, apiSecret,"https://api.coolsms.co.kr");

        String randomNum = createRandomNumber();

        redisService.save(phoneNumber, randomNum, 5);

        Message message = new Message();
        message.setFrom(fromNumber);
        message.setTo(phoneNumber);
        message.setText("[Fiurinee] 인증번호 [" + randomNum + "]를 입력해 주세요.");

        try {
            SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));
            System.out.println(response);
        } catch (Exception e) {
            throw new CustomException("SMS 전송 실패: " + e.getMessage());
        }
    }

}
