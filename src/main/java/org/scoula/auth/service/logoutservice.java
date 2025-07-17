package org.scoula.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.security.util.JwtProcessor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Log4j2
@Service
@RequiredArgsConstructor
public class LogoutService {

    private final JwtProcessor jwtProcessor;
    private final TokenBlacklistService tokenBlacklistService;
    private final SessionService sessionService;
    private final MemberMapper memberMapper;

    public void logout(String accessToken) {
        // 1. accessToken 유효성 검사 및 파싱
        String username = jwtProcessor.getUsername(accessToken);

        // 2. 블랙리스트 등록
        LocalDateTime expiresAt = jwtProcessor.getExpiration(accessToken);
        tokenBlacklistService.blacklistToken(accessToken, "access", "로그아웃", expiresAt);

        // 3. 세션 테이블에서 logoutAt 갱신 (가장 최근 세션)
        Long memberId = memberMapper.findIdByUsername(username);
        sessionService.logoutLatestSession(memberId);

        log.info("사용자({}) 로그아웃 처리 완료", username);
    }
}
