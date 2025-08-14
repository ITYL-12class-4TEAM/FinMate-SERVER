package org.scoula.notification.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.common.service.RedisService;
import org.scoula.notification.mapper.NotificationMapper;
import org.scoula.notification.service.NotificationService;
import org.scoula.notification.service.NotificationSseService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationHelper {

    private final NotificationService notificationService;


    public void notifyCommentCreated(Long postId, Long commentId, Long authorId, Boolean isAnonymous, String authorNickname, String postTitle) {
        try {
            notificationService.createCommentNotification(postId, commentId, authorId, isAnonymous, authorNickname, postTitle);
        } catch (Exception e) {
            log.error("댓글 알림 생성 실패: postId={}, commentId={}", postId, commentId, e);
        }
    }

    public void notifyLikeCreated(Long postId, Long authorId, String authorNickname, String postTitle) {
        try {
            notificationService.createLikeNotification(postId, authorId, authorNickname, postTitle);
        } catch (Exception e) {
            log.error("좋아요 알림 생성 실패: postId={}", postId, e);
        }
    }

    public void notifyHotPost(Long postId, String postTitle, String category, int likeCount) {
        try {
            notificationService.createHotPostNotification(postId, postTitle, category, likeCount);
        } catch (Exception e) {
            log.error("핫 게시글 알림 생성 실패: postId={}", postId, e);
        }
    }
}
