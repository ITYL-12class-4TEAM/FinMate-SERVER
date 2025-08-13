package org.scoula.notification.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.notification.service.NotificationSseService;
import org.scoula.security.util.JwtProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.security.Principal;

@Api(tags = "실시간 알림 SSE API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@CrossOrigin(
        originPatterns = {"http://localhost:*", "http://127.0.0.1:*"},
        allowCredentials = "true",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
@Slf4j
public class NotificationSseController {

    private final NotificationSseService notificationSseService;
    private final MemberMapper memberMapper;
    private final JwtProcessor jwtProcessor;

    @ApiOperation("SSE 연결 설정 (토큰 파라미터 방식)")
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> streamNotifications(@RequestParam(required = false) String token) {
        log.info("=== SSE 연결 요청 - 토큰 파라미터 방식 ===");

        try {
            // 토큰 검증
            if (token == null || token.trim().isEmpty()) {
                log.error("SSE 연결 실패: 토큰이 없습니다");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Bearer 토큰 형식인 경우 제거
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // JWT 토큰 유효성 검증
            if (!jwtProcessor.validateToken(token)) {
                log.error("SSE 연결 실패: 유효하지 않은 토큰");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // 토큰에서 사용자 정보 추출
            String username = jwtProcessor.getUsername(token);
            Long memberId = jwtProcessor.getMemberId(token);

            if (memberId == null) {
                log.error("SSE 연결 실패: 토큰에서 memberId를 추출할 수 없습니다");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            log.info("SSE 연결 요청 - 회원 ID: {}, 사용자명: {}", memberId, username);

            SseEmitter emitter = notificationSseService.createConnection(memberId);
            return ResponseEntity.ok(emitter);

        } catch (Exception e) {
            log.error("SSE 연결 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ApiOperation("SSE 연결 해제")
    @DeleteMapping("/stream")
    public void disconnectStream(Principal principal) {
        Long memberId = Long.valueOf(principal.getName());
        log.info("SSE 연결 해제 요청 - 회원 ID: {}", memberId);

        notificationSseService.removeConnection(memberId);
    }

    @ApiOperation("테스트 알림 전송")
    @PostMapping("/test")
    public void sendTestNotification(Principal principal) {
        String email = String.valueOf(principal.getName());
        Long memberId = memberMapper.findIdByUsername(email);
        log.info("테스트 알림 전송 - 회원 ID: {}", memberId);

        notificationSseService.sendTestNotification(memberId);
    }
}
