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
            @Param("memberId") Long memberId
    );

    long countUnreadNotifications(@Param("memberId") Long memberId);

    // 알림 읽음 처리
    int deleteNotification(@Param("id") Long id, @Param("memberId") Long memberId);
    int deleteAllUnreadNotifications(@Param("memberId") Long memberId);

    // 게시글 작성자 ID 조회
    Long selectPostAuthorId(@Param("postId") Long postId);

    boolean selectNotificationSetting(@Param("memberId") Long memberId);
    int updateNotificationSetting(@Param("memberId") Long memberId,  @Param("isEnabled") Boolean isEnabled);

    int deleteOldNotifications(@Param("daysBefore") int daysBefore);
    List<Long> selectAllActiveMemberIds();
    }
