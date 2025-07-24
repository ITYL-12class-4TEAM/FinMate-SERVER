package org.scoula.member.service;

public interface SmsService {
    boolean verifyCode(String phoneNumber, String code);
    void markPhoneAsVerified(String phoneNumber);
    void certificateSMS(String phoneNumber);
}
