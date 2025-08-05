package org.scoula.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.scoula.auth.dto.request.*;
import org.scoula.auth.dto.FindIdResponseDTO;
import org.scoula.auth.dto.TokenResponseDTO;
import org.scoula.auth.exception.AuthenticationException;
import org.scoula.auth.exception.InvalidPasswordFormatException;
import org.scoula.auth.exception.PasswordMismatchException;
import org.scoula.auth.exception.TokenValidationException;
import org.scoula.auth.service.AuthService;
import org.scoula.common.service.RedisService;
import org.scoula.member.exception.MemberNotFoundException;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.member.service.impl.SignupServiceImpl;
import org.scoula.response.ResponseCode;
import org.scoula.security.account.domain.MemberVO;
import org.scoula.security.util.JwtProcessor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.scoula.security.account.dto.AuthResultDTO;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtProcessor jwtProcessor;
    private final MemberMapper memberMapper;
    private final RedisService redisService;
    private final SignupServiceImpl signupService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public TokenResponseDTO refreshToken(String refreshToken) {
        if (!jwtProcessor.validateToken(refreshToken)) {
            throw new TokenValidationException(ResponseCode.INVALID_REFRESH_TOKEN);
        }

        String username = jwtProcessor.getUsername(refreshToken);
        Long memberId = memberMapper.findIdByUsername(username);

        if (memberId == null) {
            throw new MemberNotFoundException(ResponseCode.MEMBER_NOT_FOUND);
        }

        String redisRefresh = memberMapper.getRefreshToken(memberId);
        if (!refreshToken.equals(redisRefresh)) {
            throw new TokenValidationException(ResponseCode.REFRESH_TOKEN_MISMATCH);
        }

        String newAccessToken = jwtProcessor.generateAccessToken(memberId, username);
        String newRefreshToken = jwtProcessor.generateRefreshToken(username);
        redisService.saveAccessToken("ACCESS:" + memberId, newAccessToken);

        memberMapper.updateTokens(username, newRefreshToken);

        return new TokenResponseDTO(newAccessToken, newRefreshToken);
    }

    @Override
    public FindIdResponseDTO findUsernameByNameAndPhone(FindIdRequest request) {
        String verificationKey = "phone_verified:" + request.getPhoneNumber();
        String isVerified = redisService.get(verificationKey);
        if (!"true".equals(isVerified)) {
            throw new AuthenticationException(ResponseCode.AUTHENTICATION_REQUIRED);
        }
        String username = memberMapper.findUsernameByNameAndPhone(request.getName(), request.getPhoneNumber());
        if (username == null) {
            throw new MemberNotFoundException(ResponseCode.MEMBER_NOT_FOUND);
        }
        return new FindIdResponseDTO(username);
    }

    @Override
    public FindIdResponseDTO findPassword(FindIdRequest request) {
        String verificationKey = "phone_verified:" + request.getPhoneNumber();
        String isVerified = redisService.get(verificationKey);
        if (!"true".equals(isVerified)) {
            throw new AuthenticationException(ResponseCode.AUTHENTICATION_REQUIRED);
        }

        String username = memberMapper.findUsernameByNameAndPhone(request.getName(), request.getPhoneNumber());
        if (username == null) {
            throw new MemberNotFoundException(ResponseCode.MEMBER_NOT_FOUND);
        }
        return new FindIdResponseDTO(username);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        if (!signupService.isValidPassword(request.getNewPassword())) {
            throw new InvalidPasswordFormatException(ResponseCode.INVALID_PASSWORD_FORMAT);
        }
        if (!request.getNewPassword().equals(request.getNewPasswordCheck())) {
            throw new PasswordMismatchException(ResponseCode.PASSWORD_MISMATCH);
        }

        MemberVO member = memberMapper.selectByEmail(request.getUsername());
        if (member == null) {
            throw new MemberNotFoundException(ResponseCode.MEMBER_NOT_FOUND);
        }
        String encodedPassword = encoder.encode(request.getNewPassword());
        memberMapper.updatePassword(member.getMemberId(), encodedPassword);
    }

    @Override
    public void updateProfile(UpdateProfileRequest request) {
        signupService.updateProfile(
                request.getMemberId(),
                request.getNickname(),
                request.getReceivePushNotification()
        );
    }

    @Override
    public void withdrawMember(WithdrawRequest request) {
        MemberVO member = memberMapper.selectByEmail(request.getUsername());
        if (member == null) {
            throw new MemberNotFoundException(ResponseCode.MEMBER_NOT_FOUND);
        }
        if (!encoder.matches(request.getPassword(), member.getPassword())) {
            throw new AuthenticationException(ResponseCode.PASSWORD_MISMATCH);
        }
        if (memberMapper.deleteMember(member.getMemberId()) == 0) {
            throw new MemberNotFoundException(ResponseCode.MEMBER_WITHDRAW_FAILED);
        }
    }
    @Override
    public void checkPassword(PasswordCheckRequest request, String email) {

        MemberVO member = memberMapper.selectByEmail(email);

        if (!encoder.matches(request.getPassword(), member.getPassword())) {
            throw new AuthenticationException(ResponseCode.PASSWORD_MISMATCH);
        }
    }
    @Override
    public AuthResultDTO exchangeToken(String code) {
        try {
            String resultJson = redisService.get("code:" + code);
            if (resultJson == null) {
                throw new AuthenticationException(ResponseCode.INVALID_TOKEN);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            AuthResultDTO result = objectMapper.readValue(resultJson, AuthResultDTO.class);
            redisService.delete("code:" + code);
            return result;
        } catch (Exception e) {
            throw new AuthenticationException(ResponseCode.TOKEN_EXCHANGE_FAILED);
        }
    }

}
