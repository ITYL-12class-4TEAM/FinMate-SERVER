package org.scoula.auth.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.auth.dto.LogoutResponseDTO;
import org.scoula.auth.service.LogoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

@Api(tags = "로그아웃 API")
@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class LogoutApiController {

    private final LogoutService logoutService;

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDTO> logout(HttpServletRequest request) {
        String accessToken = extractAccessToken(request);

        try {
            logoutService.logout(accessToken);
            LogoutResponseDTO response = new LogoutResponseDTO(
                    true,
                    "로그아웃이 완료되었습니다.",
                    new LogoutResponseDTO.DataDTO(Instant.now().toString())
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.warn("[LOGOUT] 로그아웃 실패: {}", e.getMessage());
            LogoutResponseDTO response = new LogoutResponseDTO(
                    false,
                    "유효하지 않은 토큰",
                    null
            );
            return ResponseEntity.status(401).body(response);
        }
    }

    private String extractAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new RuntimeException("Authorization 헤더가 없거나 잘못되었습니다.");
    }
}