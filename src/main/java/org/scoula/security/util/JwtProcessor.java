package org.scoula.security.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Log4j2
@Component

public class JwtProcessor {

    private static final long ACCESS_TOKEN_VALID_MILLIS = 1000L * 60 * 30;
    private static final long REFRESH_TOKEN_VALID_MILLIS = 1000L * 60 * 60 * 24 * 7;

    @Value("${jwt.secret_key}")
    private String secretKeyRaw;

    private Key key;

    @PostConstruct
    public void init() {
        log.info("[JWT] Secret Key Raw = {}", secretKeyRaw);
        this.key = Keys.hmacShaKeyFor(secretKeyRaw.getBytes(StandardCharsets.UTF_8));
        log.info("[JWT] Secret Key 초기화 완료, length = {}", secretKeyRaw.length());
    }

    // 토큰 생성
    public String generateAccessToken(Long memberId, String username) {
        return Jwts.builder()
                .setSubject(username)
                .claim("memberId", memberId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALID_MILLIS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 memberId 추출
    public Long getMemberId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("memberId", Long.class);
    }

    public String generateRefreshToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALID_MILLIS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }



    public String getUsername(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.error("[JWT 검증 실패] {}", e.getMessage());
            return false;
        }
    }

    public LocalDateTime getExpiration(String token) {
        Date date = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
