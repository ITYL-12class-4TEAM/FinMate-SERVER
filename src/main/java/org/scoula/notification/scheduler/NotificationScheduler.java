package org.scoula.notification.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.notification.mapper.NotificationMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationMapper notificationMapper;


    @Scheduled(cron = "0 0 2 * * *")
    public void deleteOldNotifications() {
        try {
            int deletedCount = notificationMapper.deleteOldNotifications(90);
            log.info("오래된 알림 삭제 완료: {}개", deletedCount);
        } catch (Exception e) {
            log.error("오래된 알림 삭제 실패", e);
        }
    }
}
