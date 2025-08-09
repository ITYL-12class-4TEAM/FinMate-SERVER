package org.scoula.notification.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.common.service.RedisService;
import org.scoula.notification.domain.NotificationType;
import org.scoula.notification.dto.response.NotificationResponseDTO;
import org.scoula.notification.service.NotificationSseService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSseServiceImpl implements NotificationSseService {

    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    // SSE 연결 관리를 위한 메모리 저장소
    private final Map<Long, SseEmitter> connections = new ConcurrentHashMap<>();

    // 하트비트를 위한 스케줄러
    private ScheduledExecutorService heartbeatScheduler;

    private static final Long TIMEOUT = 30 * 60 * 1000L; // 30분
    private static final String REDIS_NOTIFICATION_CHANNEL = "notification:";
    private static final String REDIS_CONNECTION_KEY = "sse_connections:";

    @PostConstruct
    public void init() {
        // 하트비트 스케줄러 초기화 (30초마다)
        heartbeatScheduler = Executors.newScheduledThreadPool(1);
        heartbeatScheduler.scheduleAtFixedRate(this::sendHeartbeat, 30, 30, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void destroy() {
        if (heartbeatScheduler != null) {
            heartbeatScheduler.shutdown();
        }
        // 모든 연결 종료
        connections.values().forEach(SseEmitter::complete);
        connections.clear();
    }

    @Override
    public SseEmitter createConnection(Long memberId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);

        // 기존 연결이 있다면 종료
        removeConnection(memberId);
        connections.put(memberId, emitter);

        redisService.save(REDIS_CONNECTION_KEY + memberId, "connected", 1800); // 30분

        emitter.onCompletion(() -> {
            log.info("SSE 연결 완료 - 회원 ID: {}", memberId);
            removeConnection(memberId);
        });

        emitter.onTimeout(() -> {
            log.info("SSE 연결 타임아웃 - 회원 ID: {}", memberId);
            removeConnection(memberId);
        });

        emitter.onError((ex) -> {
            log.error("SSE 연결 오류 - 회원 ID: {}, 오류: {}", memberId, ex.getMessage());
            removeConnection(memberId);
        });

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("연결이 성공적으로 설정되었습니다.")
                    .id(String.valueOf(System.currentTimeMillis())));

            log.info("SSE 연결 생성 완료 - 회원 ID: {}", memberId);
        } catch (IOException e) {
            log.error("초기 연결 메시지 전송 실패 - 회원 ID: {}", memberId, e);
            removeConnection(memberId);
        }

        return emitter;
    }

    @Override
    public void removeConnection(Long memberId) {
        SseEmitter emitter = connections.remove(memberId);
        if (emitter != null) {
            try {
                emitter.complete();
            } catch (Exception e) {
                log.warn("SSE 연결 종료 중 오류 - 회원 ID: {}", memberId, e);
            }
        }

        redisService.delete(REDIS_CONNECTION_KEY + memberId);
        log.info("SSE 연결 제거 완료 - 회원 ID: {}", memberId);
    }

    @Override
    public void sendNotificationToMember(Long memberId, NotificationResponseDTO notification) {
        SseEmitter emitter = connections.get(memberId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notification)
                        .id(String.valueOf(notification.getNotificationId())));

                log.info("실시간 알림 전송 성공 - 회원 ID: {}, 알림 ID: {}", memberId, notification.getNotificationId());

                // Redis에도 알림 정보 저장 (오프라인 사용자를 위해)
                String redisKey = REDIS_NOTIFICATION_CHANNEL + memberId;
                redisService.save(redisKey, objectMapper.writeValueAsString(notification), 3600); // 1시간

            } catch (IOException e) {
                log.error("실시간 알림 전송 실패 - 회원 ID: {}, 알림 ID: {}", memberId, notification.getNotificationId(), e);
                removeConnection(memberId);
            } catch (Exception e) {
                log.error("Redis 저장 실패 - 회원 ID: {}", memberId, e);
            }
        } else {
            log.debug("SSE 연결 없음 - 회원 ID: {}, Redis에 저장", memberId);

            // 연결이 없어도 Redis에 저장
            try {
                String redisKey = REDIS_NOTIFICATION_CHANNEL + memberId;
                redisService.save(redisKey, objectMapper.writeValueAsString(notification), 3600);
            } catch (Exception e) {
                log.error("오프라인 알림 저장 실패 - 회원 ID: {}", memberId, e);
            }
        }
    }

    @Override
    public void broadcastNotification(NotificationResponseDTO notification) {
        log.info("브로드캐스트 알림 전송 시작 - 연결된 회원 수: {}", connections.size());

        connections.forEach((memberId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("broadcast")
                        .data(notification)
                        .id(String.valueOf(System.currentTimeMillis())));
            } catch (IOException e) {
                log.error("브로드캐스트 전송 실패 - 회원 ID: {}", memberId, e);
                removeConnection(memberId);
            }
        });
    }

    @Override
    public void sendTestNotification(Long memberId) {
        NotificationResponseDTO testNotification = NotificationResponseDTO.builder()
                .notificationId(999L)
                .title("테스트 알림")
                .message("SSE 연결 테스트 알림입니다.")
                .type(NotificationType.SYSTEM)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        sendNotificationToMember(memberId, testNotification);
    }

    @Override
    public boolean isConnected(Long memberId) {
        boolean inMemory = connections.containsKey(memberId);
        boolean inRedis = redisService.exists(REDIS_CONNECTION_KEY + memberId);
        return inMemory || inRedis;
    }

    @Override
    public int getConnectedMemberCount() {
        return connections.size();
    }


    private void sendHeartbeat() {
        connections.entrySet().removeIf(entry -> {
            Long memberId = entry.getKey();
            SseEmitter emitter = entry.getValue();

            try {
                emitter.send(SseEmitter.event()
                        .name("heartbeat")
                        .data("ping")
                        .id(String.valueOf(System.currentTimeMillis())));
                return false;
            } catch (IOException e) {
                log.debug("하트비트 전송 실패 - 회원 ID: {}, 연결 제거", memberId);
                try {
                    emitter.complete();
                } catch (Exception ex) {
                    // 무시
                }
                redisService.delete(REDIS_CONNECTION_KEY + memberId);
                return true; // 실패하면 제거
            }
        });
    }
}
