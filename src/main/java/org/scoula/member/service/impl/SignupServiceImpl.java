package org.scoula.member.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.common.service.RedisService;
import org.scoula.member.dto.RegisterDTO;
import org.scoula.member.dto.SocialRegisterDTO;
import org.scoula.member.exception.SignupException;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.member.service.SignupService;
import org.scoula.response.ResponseCode;
import org.scoula.security.account.domain.MemberVO;
import org.scoula.security.account.dto.AuthResultDTO;
import org.scoula.security.account.dto.UserInfoDTO;
import org.scoula.security.util.JwtProcessor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class SignupServiceImpl implements SignupService {
    private final MemberMapper memberMapper;
    private final RedisService redisService;
    private final JwtProcessor jwtProcessor;

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

    @Override
    public void socialRegister(SocialRegisterDTO dto) {
        try {
            // 닉네임 중복 체크
            MemberVO existingNickname = memberMapper.selectByNickname(dto.getNickname());
            if (existingNickname != null) {
                throw new SignupException(ResponseCode.DUPLICATED_NICKNAME);
            }

            Long memberId = memberMapper.getMemberIdByEmail(dto.getEmail());

            // 생년월일 파싱
            Date birthDate = null;
            if (dto.getBirthDate() != null && !dto.getBirthDate().isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    birthDate = sdf.parse(dto.getBirthDate());
                } catch (ParseException e) {
                    throw new SignupException(ResponseCode.INVALID_BIRTHDATE_FORMAT);
                }
            }

            MemberVO member = MemberVO.builder()
                    .memberId(memberId)
                    .nickname(dto.getNickname())
                    .birthDate(birthDate)
                    .gender(dto.getGender())
                    .receivePushNotification(Boolean.TRUE.equals(dto.getReceivePushNotification()))
                    .isNewMember(false) // 소셜 회원가입 완료 시 false
                    .updatedAt(new Date())
                    .build();

            memberMapper.completeSocialSignup(member);

        } catch (Exception e) {
            if (e instanceof SignupException) {
                throw e;
            }
            throw new SignupException(ResponseCode.SERVER_ERROR);
        }
    }

    @Override
    public AuthResultDTO createAuthResult(String email) {
        try {
            // 이메일로 회원 정보 조회
            MemberVO member = memberMapper.selectByEmail(email);
            if (member == null) {
                throw new SignupException(ResponseCode.MEMBER_NOT_FOUND);
            }

            // JWT 토큰 생성
            String accessToken = jwtProcessor.generateAccessToken(member.getMemberId(), member.getEmail());
            String refreshToken = jwtProcessor.generateRefreshToken(member.getEmail());

            // Redis에 토큰 저장
            redisService.saveAccessToken(member.getMemberId().toString(), accessToken);
            memberMapper.updateTokens(member.getEmail(), refreshToken);

            // AuthResultDTO 생성
            return new AuthResultDTO(
                accessToken,
                refreshToken,
                UserInfoDTO.of(member),
                false
            );

        } catch (Exception e) {
            log.error("AuthResult 생성 중 오류 발생", e);
            throw new SignupException(ResponseCode.SERVER_ERROR);
        }
    }
}