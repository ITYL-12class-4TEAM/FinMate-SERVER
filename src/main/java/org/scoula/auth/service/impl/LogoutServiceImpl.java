package org.scoula.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.auth.exception.TokenValidationException;
import org.scoula.auth.service.LogoutService;
import org.scoula.common.service.RedisService;
import org.scoula.member.exception.MemberNotFoundException;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.response.ResponseCode;
import org.scoula.security.util.JwtProcessor;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService {

    private final JwtProcessor jwtProcessor;
    private final RedisService redisService;
    private final MemberMapper memberMapper;

    @Override
    public void logout(String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new TokenValidationException(ResponseCode.INVALID_TOKEN);
        }

        if (!jwtProcessor.validateToken(accessToken)) {
            throw new TokenValidationException(ResponseCode.INVALID_TOKEN);
        }

        String username = jwtProcessor.getUsername(accessToken);
        if (username == null || username.isEmpty()) {
            throw new TokenValidationException(ResponseCode.INVALID_TOKEN);
        }

        Long memberId = jwtProcessor.getMemberId(accessToken);
        if (memberId == null) {
            throw new MemberNotFoundException(ResponseCode.MEMBER_NOT_FOUND);
        }

        redisService.deleteAccessToken(memberId.toString());
        memberMapper.clearRefreshToken(username);

        log.info("사용자({}) 로그아웃 처리 완료", username);
    }
}
