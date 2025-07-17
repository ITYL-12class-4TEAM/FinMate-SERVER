package org.scoula.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.auth.domain.TokenBlacklist;
import org.scoula.auth.mapper.TokenBlacklistMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Log4j2
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final TokenBlacklistMapper tokenBlacklistMapper;

    public void blacklistToken(String token, String tokenType, String reason, LocalDateTime expiresAt) {
        TokenBlacklist entry = new TokenBlacklist();
        entry.setToken(token);
        entry.setTokenType(tokenType);
        entry.setReason(reason);
        entry.setRevokedAt(LocalDateTime.now());
        entry.setExpiresAt(expiresAt);
        tokenBlacklistMapper.insertBlacklistToken(entry);
    }

    public boolean isBlacklisted(String token) {
        return tokenBlacklistMapper.isTokenBlacklisted(token);
    }

}
