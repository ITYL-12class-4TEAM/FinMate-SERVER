package org.scoula.notification.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.notification.domain.NotificationSettingVO;
import org.scoula.notification.domain.NotificationVO;
import org.scoula.notification.domain.NotificationType;

import java.util.List;
import java.util.Map;

@Mapper
public interface NotificationMapper {

    // 알림 CRUD
    int insertNotification(NotificationVO notification);
    NotificationVO selectNotificationById(@Param("id") Long id, @Param("memberId") Long memberId);
    List<NotificationVO> selectNotificationsByMemberId(
            @Param("memberId") Long memberId,
            @Param("type") NotificationType type,
            @Param("isRead") Boolean isRead,
            @Param("offset") int offset,
            @Param("limit") int limit
    );
    long countNotificationsByMemberId(
            @Param("memberId") Long memberId,
            @Param("type") NotificationType type,
            @Param("isRead") Boolean isRead
    );
    long countUnreadNotifications(@Param("memberId") Long memberId);

    // 알림 읽음 처리
    int updateNotificationAsRead(@Param("id") Long id, @Param("memberId") Long memberId);
    int updateAllNotificationsAsRead(@Param("memberId") Long memberId);


    boolean selectNotificationSetting(@Param("memberId") Long memberId);
    int updateNotificationSetting(@Param("memberId") Long memberId,  @Param("isEnabled") Boolean isEnabled);

    int deleteOldNotifications(@Param("daysBefore") int daysBefore);

    // 관심 사용자 조회 (게시글/댓글 작성자 등)
    List<Long> selectInterestedMemberIds(@Param("postId") Long postId, @Param("excludeMemberId") Long excludeMemberId);
}
