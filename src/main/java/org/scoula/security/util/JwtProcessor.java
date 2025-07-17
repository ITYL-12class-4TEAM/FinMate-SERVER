package org.scoula.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProcessor {

    // 토큰 유효 시간
    private static final long ACCESS_TOKEN_VALID_MILLIS = 1000L * 60 * 30;   // 30분
    private static final long REFRESH_TOKEN_VALID_MILLIS = 1000L * 60 * 60 * 24 * 7; // 7일

    // 보안 키 (운영 시 외부 주입 필요)
    private final String secretKey = "충분히 긴 임의의(랜덤한) 비밀키 문자열 배정 ";
    private final Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));


    public String generateAccessToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALID_MILLIS))
                .signWith(key)
                .compact();
    }


    public String generateRefreshToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALID_MILLIS))
                .signWith(key)
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
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
        return true;
    }
}
