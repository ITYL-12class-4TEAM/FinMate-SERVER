package org.scoula.auth.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.auth.dto.TokenResponseDTO;
import org.scoula.security.util.JwtProcessor;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.common.service.RedisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class TokenApiController {

    private final JwtProcessor jwtProcessor;
    private final MemberMapper memberMapper;
    private final RedisService redisService;

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDTO> refresh(@RequestBody RefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtProcessor.validateToken(refreshToken)) {
            return ResponseEntity.status(401)
                    .body(new TokenResponseDTO(false, "유효하지 않은 리프레시 토큰", null));
        }

        //  사용자 정보 추출
        String username = jwtProcessor.getUsername(refreshToken);
        Long memberId = memberMapper.findIdByUsername(username);

        //  Redis에 저장된 refresh 토큰과 일치하는지 확인
        String redisRefresh = memberMapper.getRefreshToken(memberId);
        if (!refreshToken.equals(redisRefresh)) {
            return ResponseEntity.status(401)
                    .body(new TokenResponseDTO(false, "리프레시 토큰이 일치하지 않습니다.", null));
        }

        //  새로운 access 토큰 발급
        String newAccessToken = jwtProcessor.generateAccessToken(username);

        //  Redis에 access 토큰 갱신
        redisService.saveAccessToken("ACCESS:" + memberId, newAccessToken);

        return ResponseEntity.ok(
                new TokenResponseDTO(true, "새로운 액세스 토큰이 발급되었습니다.",
                        new TokenResponseDTO.DataDTO(newAccessToken))
        );
    }

    // 요청 DTO
    public static class RefreshRequest {
        private String refreshToken;
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }
}