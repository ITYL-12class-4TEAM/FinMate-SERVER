package org.scoula.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.security.util.JwtProcessor;
import org.scoula.common.service.RedisService;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class LogoutService {

    private final JwtProcessor jwtProcessor;
    private final RedisService redisService;
    private final MemberMapper memberMapper;

    public void logout(String accessToken) {

        String username = jwtProcessor.getUsername(accessToken);
        Long memberId = memberMapper.findIdByUsername(username);


        redisService.deleteAccessToken(memberId.toString());
        memberMapper.clearRefreshToken(username);

        log.info("사용자({}) 로그아웃 처리 완료", username);
    }
}
