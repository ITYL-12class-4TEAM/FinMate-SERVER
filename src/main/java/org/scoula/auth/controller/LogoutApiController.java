package org.scoula.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.auth.service.LogoutService;
import org.scoula.security.util.JwtProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class LogoutApiController {

    private final LogoutService logoutService;

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String accessToken = extractAccessToken(request);

        try {
            logoutService.logout(accessToken);
            return ResponseEntity.ok("로그아웃 완료");
        } catch (Exception e) {
            log.warn("[LOGOUT] 로그아웃 실패: {}", e.getMessage());
            log.warn("[LOGOUT] 로그아웃 실패: {}", accessToken);
            return ResponseEntity.status(401).body("유효하지 않은 토큰");
        }
    }

    private String extractAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // "Bearer " 제거
        }
        throw new RuntimeException("Authorization 헤더가 없거나 잘못되었습니다.");
    }
}
