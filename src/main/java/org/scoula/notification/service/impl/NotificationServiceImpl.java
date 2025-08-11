package org.scoula.notification.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.common.service.RedisService;
import org.scoula.notification.domain.NotificationVO;
import org.scoula.notification.domain.NotificationType;
import org.scoula.notification.dto.request.NotificationCreateRequest;
import org.scoula.notification.dto.request.NotificationSettingUpdateRequest;
import org.scoula.notification.dto.response.NotificationListResponseDTO;
import org.scoula.notification.dto.response.NotificationResponseDTO;
import org.scoula.notification.dto.response.PaginationDTO;
import org.scoula.notification.exception.NotificationNotFoundException;
import org.scoula.notification.exception.NotificationUnauthorizedAccessException;
import org.scoula.notification.mapper.NotificationMapper;
import org.scoula.notification.service.NotificationService;
import org.scoula.response.ResponseCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public NotificationListResponseDTO getNotifications(Long memberId, int page, int size, NotificationType type, Boolean isRead) {
        int offset = (page - 1) * size;

        List<NotificationVO> notifications = notificationMapper.selectNotificationsByMemberId(
                memberId, type, isRead, offset, size);

        long totalCount = notificationMapper.countNotificationsByMemberId(memberId, type, isRead);
        long unreadCount = notificationMapper.countUnreadNotifications(memberId);

        List<NotificationResponseDTO> notificationDTOs = notifications.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        return NotificationListResponseDTO.builder()
                .notifications(notificationDTOs)
                .pagination(PaginationDTO.builder()
                        .currentPage(page)
                        .totalPages((int) Math.ceil((double) totalCount / size))
                        .totalCount(totalCount)
                        .unreadCount(unreadCount)
                        .build())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long memberId) {
        String cacheKey = "unread_count:" + memberId;
        String cachedCount = redisService.get(cacheKey);

        if (cachedCount != null) {
            return Long.parseLong(cachedCount);
        }

        long count = notificationMapper.countUnreadNotifications(memberId);
        redisService.save(cacheKey, String.valueOf(count), 300); // 5분 캐시

        return count;
    }

    @Override
    public NotificationResponseDTO markAsRead(Long notificationId, Long memberId) {
        NotificationVO notification = notificationMapper.selectNotificationById(notificationId, memberId);
        if (notification == null) {
            throw new NotificationNotFoundException(ResponseCode.NOTIFICATION_NOT_FOUND);
        }

        if (!notification.getMemberId().equals(memberId)) {
            throw new NotificationUnauthorizedAccessException(ResponseCode.NOTIFICATION_UNAUTHORIZED_ACCESS);
        }

        if (Boolean.TRUE.equals(notification.getIsRead())) {
            throw new NotificationNotFoundException(ResponseCode.NOTIFICATION_ALREADY_READ);
        }

        notificationMapper.updateNotificationAsRead(notificationId, memberId);

        // Redis 캐시 무효화
        String cacheKey = "unread_count:" + memberId;
        redisService.delete(cacheKey);

        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());

        return convertToResponseDTO(notification);
    }

    @Override
    public void markAllAsRead(Long memberId) {
        notificationMapper.updateAllNotificationsAsRead(memberId);

        // Redis 캐시 무효화
        String cacheKey = "unread_count:" + memberId;
        redisService.delete(cacheKey);
    }

    @Override
    public void createNotification(NotificationCreateRequest request) {
        // 알림 설정 확인
        if (!getNotificationSettings(request.getMemberId())) {
            log.debug("알림이 비활성화됨: memberId={}, type={}", request.getMemberId(), request.getType());
            return;
        }

        String relatedDataJson = null;
        if (request.getRelatedData() != null) {
            try {
                relatedDataJson = objectMapper.writeValueAsString(request.getRelatedData());
            } catch (JsonProcessingException e) {
                log.error("관련 데이터 JSON 변환 실패", e);
            }
        }

        NotificationVO notification = NotificationVO.builder()
                .memberId(request.getMemberId())
                .type(request.getType())
                .title(request.getTitle())
                .message(request.getMessage())
                .targetUrl(request.getTargetUrl())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .relatedData(relatedDataJson)
                .build();

        notificationMapper.insertNotification(notification);

        // Redis 캐시 무효화
        String cacheKey = "unread_count:" + request.getMemberId();
        redisService.delete(cacheKey);

        log.info("알림 생성 완료: memberId={}, type={}", request.getMemberId(), request.getType());
    }

    @Override
    public void createCommentNotification(Long postId, Long commentId, Long authorId, String authorNickname, String postTitle) {

        // 게시글 작성자 ID 조회 (authorId는 댓글 작성자이므로 게시글 작성자를 따로 조회해야 함)
        Long postAuthorId = notificationMapper.selectPostAuthorId(postId);

        if (postAuthorId == null) {
            log.warn("게시글 작성자를 찾을 수 없습니다: postId={}", postId);
            return;
        }

        // 자신의 게시글에 자신이 댓글을 단 경우는 알림 생성하지 않음
        if (postAuthorId.equals(authorId)) {
            log.debug("자신의 게시글에 자신이 댓글을 달았으므로 알림을 생성하지 않습니다: postId={}, authorId={}", postId, authorId);
            return;
        }

        Map<String, Object> relatedData = new HashMap<>();
        relatedData.put("postId", postId);
        relatedData.put("postTitle", postTitle);
        relatedData.put("commentId", commentId);
        relatedData.put("authorNickname", authorNickname);

        NotificationCreateRequest request = NotificationCreateRequest.builder()
                .memberId(postAuthorId) // 게시글 작성자에게 알림
                .type(NotificationType.POST_COMMENT)
                .title("새 댓글 알림")
                .message(authorNickname + "님이 회원님의 게시글에 댓글을 달았습니다")
                .targetUrl("/posts/" + postId + "#comment-" + commentId)
                .relatedData(relatedData)
                .build();

        createNotification(request);

        log.info("댓글 알림 생성 완료: postId={}, commentId={}, postAuthor={}, commentAuthor={}",
                postId, commentId, postAuthorId, authorId);
    }

    @Override
    public void createLikeNotification(Long postId, Long authorId, String authorNickname, String postTitle) {
        // 게시글 작성자에게 알림 발송
        List<Long> interestedMemberIds = notificationMapper.selectInterestedMemberIds(postId, authorId);

        for (Long memberId : interestedMemberIds) {
            Map<String, Object> relatedData = new HashMap<>();
            relatedData.put("postId", postId);
            relatedData.put("postTitle", postTitle);
            relatedData.put("authorNickname", authorNickname);

            NotificationCreateRequest request = NotificationCreateRequest.builder()
                    .memberId(memberId)
                    .type(NotificationType.POST_LIKE)
                    .title("좋아요 알림")
                    .message(authorNickname + "님이 회원님의 게시글을 좋아합니다")
                    .targetUrl("/posts/" + postId)
                    .relatedData(relatedData)
                    .build();

            createNotification(request);
        }
    }

    @Override
    public void createHotPostNotification(Long postId, String postTitle, String category, int likeCount) {

        Map<String, Object> relatedData = new HashMap<>();
        relatedData.put("postId", postId);
        relatedData.put("postTitle", postTitle);
        relatedData.put("category", category);
        relatedData.put("likeCount", likeCount);

        log.info("핫 게시글 알림 생성: postId={}, category={}, likeCount={}", postId, category, likeCount);
    }

    @Override
    public boolean getNotificationSettings(Long memberId) {
        boolean setting = notificationMapper.selectNotificationSetting(memberId);
        return Boolean.TRUE.equals(setting);
    }

    @Override
    public void updateNotificationSettings(Long memberId, NotificationSettingUpdateRequest request) {
        notificationMapper.updateNotificationSetting(memberId, request.getIsEnabled());
    }



    private NotificationResponseDTO convertToResponseDTO(NotificationVO notification) {
        Map<String, Object> relatedData = null;
        if (notification.getRelatedData() != null) {
            try {
                relatedData = objectMapper.readValue(notification.getRelatedData(), Map.class);
            } catch (JsonProcessingException e) {
                log.error("관련 데이터 JSON 파싱 실패", e);
            }
        }

        return NotificationResponseDTO.builder()
                .notificationId(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .targetUrl(notification.getTargetUrl())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .relatedData(relatedData)
                .build();
    }
}