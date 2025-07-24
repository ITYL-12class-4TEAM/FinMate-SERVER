package org.scoula.member.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.scoula.common.service.RedisService;
import org.scoula.member.dto.RegisterDTO;
import org.scoula.member.exception.SignupException;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.member.service.SignupService;
import org.scoula.response.ResponseCode;
import org.scoula.security.account.domain.MemberVO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignupServiceImpl implements SignupService {
    private final MemberMapper memberMapper;
    private final RedisService redisService;

    @Override
    public boolean isValidPassword(String password) {
        // 8자 이상, 영문/숫자/특수문자 각각 1개 이상
        String pattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-]).{8,}$";
        return password != null && password.matches(pattern);
    }

    @Override
    public void updateProfile(Long memberId, String nickname, Boolean receivePushNotification) {
        MemberVO member = MemberVO.builder()
                .memberId(memberId)
                .nickname(nickname)
                .receivePushNotification(Boolean.TRUE.equals(receivePushNotification))
                .build();
        int updated = memberMapper.updateProfile(member);
        if (updated <= 0) {
            throw new SignupException(ResponseCode.PROFILE_UPDATE_FAILED);
        }
    }

    @Override
    public void register(RegisterDTO dto, String phoneNumber) {
        Date birthDate = null;
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (dto.getBirthDate() != null && !dto.getBirthDate().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                birthDate = sdf.parse(dto.getBirthDate());
            } catch (ParseException e) {
                throw new SignupException(ResponseCode.INVALID_BIRTHDATE_FORMAT);
            }
        }
        String verificationKey = "phone_verified:" + phoneNumber;
        String isVerified = redisService.get(verificationKey);
        if (!"true".equals(isVerified)) {
            throw new SignupException(ResponseCode.PHONE_NOT_VERIFIED);
        }

        if (!isValidPassword(dto.getPassword())) {
            throw new SignupException(ResponseCode.INVALID_PASSWORD_FORMAT);
        }

        if (!dto.getPassword().equals(dto.getPasswordCheck())) {
            throw new SignupException(ResponseCode.PASSWORD_MISMATCH);
        }

        if (!Boolean.TRUE.equals(dto.getTermsRequired1()) || !Boolean.TRUE.equals(dto.getTermsRequired2())) {
            throw new SignupException(ResponseCode.REQUIRED_TERMS_NOT_AGREED);
        }


        if (dto.getUsername() == null || dto.getUsername().isEmpty() ||
                dto.getPassword() == null || dto.getPassword().isEmpty() ||
                dto.getPasswordCheck() == null || dto.getPasswordCheck().isEmpty() ||
                dto.getNickname() == null || dto.getNickname().isEmpty() ||
                dto.getEmail() == null || dto.getEmail().isEmpty() ||
                dto.getPhoneNumber() == null || dto.getPhoneNumber().isEmpty() ||
                dto.getBirthDate() == null || dto.getBirthDate().isEmpty() ||
                dto.getGender() == null || dto.getGender().isEmpty()) {
            throw new SignupException(ResponseCode.MISSING_REQUIRED_FIELDS);
        }

        // MemberVO 생성 및 저장
        MemberVO member = MemberVO.builder()
                .username(dto.getUsername())
                .password(new BCryptPasswordEncoder().encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .birthDate(birthDate)
                .gender(dto.getGender())
                .receivePushNotification(Boolean.TRUE.equals(dto.getReceive_push_notification()))
                .build();

        memberMapper.insert(member);
        redisService.delete(verificationKey);
    }
}