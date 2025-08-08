package org.scoula.notification.service;

import org.scoula.notification.dto.response.NotificationResponseDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationSseService {

    SseEmitter createConnection(Long memberId);

    void removeConnection(Long memberId);
    void sendNotificationToMember(Long memberId, NotificationResponseDTO notification);
    void broadcastNotification(NotificationResponseDTO notification);
    void sendTestNotification(Long memberId);

    boolean isConnected(Long memberId);

    int getConnectedMemberCount();
}
