package org.scoula.notification.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.notification.service.NotificationSseService;
import org.springframework.http.MediaType;
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

    @ApiOperation("SSE 연결 설정")
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(Principal principal) {
        System.out.println("=== SSE 엔드포인트 호출됨 ===");
        String email = String.valueOf(principal.getName());
        Long memberId = memberMapper.findIdByUsername(email);
        log.info("SSE 연결 요청 - 회원 ID: {}", email);
        return notificationSseService.createConnection(memberId);
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
