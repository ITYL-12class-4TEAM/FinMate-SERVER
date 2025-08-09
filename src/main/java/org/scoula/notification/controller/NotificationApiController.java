package org.scoula.notification.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.scoula.notification.domain.NotificationType;
import org.scoula.notification.dto.request.NotificationSettingUpdateRequest;
import org.scoula.notification.dto.response.NotificationListResponseDTO;
import org.scoula.notification.dto.response.NotificationResponseDTO;
import org.scoula.notification.service.NotificationService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@Api(tags = "알림 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationApiController {

    private final NotificationService notificationService;

    @ApiOperation("알림 목록 조회")
    @GetMapping
    public ApiResponse<NotificationListResponseDTO> getNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) NotificationType type,
            @RequestParam(required = false) Boolean isRead,
            Principal principal) {

        Long memberId = Long.valueOf(principal.getName());
        NotificationListResponseDTO response = notificationService.getNotifications(memberId, page, size, type, isRead);

        return ApiResponse.success(ResponseCode.NOTIFICATION_LIST_SUCCESS, response);
    }

    @ApiOperation("읽지 않은 알림 수 조회")
    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount(Principal principal) {
        Long memberId = Long.valueOf(principal.getName());
        long unreadCount = notificationService.getUnreadCount(memberId);

        return ApiResponse.success(ResponseCode.NOTIFICATION_LIST_SUCCESS, unreadCount);
    }

    @ApiOperation("특정 알림 읽음 처리")
    @PutMapping("/{notificationId}/read")
    public ApiResponse<NotificationResponseDTO> markAsRead(
            @PathVariable Long notificationId,
            Principal principal) {

        Long memberId = Long.valueOf(principal.getName());
        NotificationResponseDTO response = notificationService.markAsRead(notificationId, memberId);

        return ApiResponse.success(ResponseCode.NOTIFICATION_READ_SUCCESS, response);
    }

    @ApiOperation("모든 알림 읽음 처리")
    @PutMapping("/read-all")
    public ApiResponse<?> markAllAsRead(Principal principal) {
        Long memberId = Long.valueOf(principal.getName());
        notificationService.markAllAsRead(memberId);

        return ApiResponse.success(ResponseCode.NOTIFICATION_READ_ALL_SUCCESS);
    }

    @ApiOperation("알림 설정 조회")
    @GetMapping("/settings")
    public ApiResponse<?> getNotificationSettings(Principal principal) {
        Long memberId = Long.valueOf(principal.getName());
        boolean settings = notificationService.getNotificationSettings(memberId);

        return ApiResponse.success(ResponseCode.NOTIFICATION_SETTINGS_GET_SUCCESS, settings);
    }

    @ApiOperation("알림 설정 업데이트")
    @PutMapping("/settings")
    public ApiResponse<?> updateNotificationSettings(
            @RequestBody NotificationSettingUpdateRequest request,
            Principal principal) {

        Long memberId = Long.valueOf(principal.getName());
        notificationService.updateNotificationSettings(memberId, request);

        return ApiResponse.success(ResponseCode.NOTIFICATION_SETTINGS_UPDATE_SUCCESS);
    }
}
