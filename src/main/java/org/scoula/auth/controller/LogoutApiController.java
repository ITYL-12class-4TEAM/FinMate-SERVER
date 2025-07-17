package org.scoula.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.auth.service.LogoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class LogoutController {

    private final LogoutService logoutService;

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String accessToken = extractAccessToken(request);
        logoutService.logout(accessToken);
        return ResponseEntity.ok("로그아웃 완료");
    }

    private String extractAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // "Bearer " 제거
        }
        throw new RuntimeException("Authorization 헤더가 없거나 잘못되었습니다.");
    }
}
