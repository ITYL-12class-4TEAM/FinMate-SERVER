package org.scoula.notification.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.notification.service.NotificationSseService;
import org.scoula.security.util.JwtProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.security.Principal;

@Api(tags = "실시간 알림 SSE API", description = "회원 실시간 알림 스트림 및 테스트용 API")
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
    private final JwtProcessor jwtProcessor;

    @ApiOperation(
            value = "SSE 연결 설정",
            notes = "클라이언트가 토큰을 파라미터로 전달하여 실시간 알림 스트림(SSE)에 연결합니다."
    )
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> streamNotifications(
            @ApiParam(value = "JWT 인증 토큰", required = true)
            @RequestParam(required = false) String token
    ) {

        try {
            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            if (!jwtProcessor.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Long memberId = jwtProcessor.getMemberId(token);

            if (memberId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            SseEmitter emitter = notificationSseService.createConnection(memberId);
            return ResponseEntity.ok(emitter);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ApiOperation(
            value = "SSE 연결 해제",
            notes = "실시간 알림 스트림 연결을 해제합니다."
    )
    @DeleteMapping("/stream")
    public void disconnectStream(
            @ApiParam(value = "인증된 사용자 정보", required = true)
            Principal principal
    ) {
        Long memberId = Long.valueOf(principal.getName());
        notificationSseService.removeConnection(memberId);
    }

}