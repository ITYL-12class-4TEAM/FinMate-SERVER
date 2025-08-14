package org.scoula.notification.service;

import org.scoula.notification.domain.NotificationVO;
import org.scoula.notification.dto.request.NotificationCreateRequest;
import org.scoula.notification.dto.request.NotificationSettingUpdateRequest;
import org.scoula.notification.dto.response.NotificationResponseDTO;


import java.util.List;

public interface NotificationService {

    // 알림 목록 조회
    List<NotificationResponseDTO> getNotifications(Long memberId);

    void markAsRead(Long notificationId, Long memberId);
    void markAllAsRead(Long memberId);
    NotificationVO createNotification(NotificationCreateRequest request);

    // 댓글 알림 생성
    void createCommentNotification(Long postId, Long commentId, Long authorId, Boolean isAnonymous, String authorNickname, String postTitle);

    // 좋아요 알림 생성
    void createLikeNotification(Long postId, Long authorId, String authorNickname, String postTitle);

    // 핫 게시글 알림 생성
    void createHotPostNotification(Long postId, String postTitle, String category, int likeCount);

    // 알림 설정 관리
    boolean getNotificationSettings(Long memberId);
    void updateNotificationSettings(Long memberId, NotificationSettingUpdateRequest request);

    long getUnreadCount(Long memberId);
}
