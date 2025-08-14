package org.scoula.notification.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.notification.dto.request.NotificationSettingUpdateRequest;
import org.scoula.notification.dto.response.NotificationResponseDTO;
import org.scoula.notification.service.NotificationService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Log4j2
@Api(tags = "알림 관리 API", description = "회원 알림 및 알림 설정 관리용 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationApiController {

    private final NotificationService notificationService;
    private final MemberMapper memberMapper;

    @ApiOperation(
            value = "알림 목록 조회",
            notes = "회원의 알림 목록을 조회합니다."
    )
    @GetMapping
    public ApiResponse<List<NotificationResponseDTO>> getNotifications(
            @ApiParam(value = "인증된 사용자 정보", required = true) Principal principal
    ) {
        String email = String.valueOf(principal.getName());
        Long memberId = memberMapper.findIdByUsername(email);

        return ApiResponse.success(ResponseCode.NOTIFICATION_LIST_SUCCESS, notificationService.getNotifications(memberId));
    }

    @ApiOperation(
            value = "읽지 않은 알림 수 조회",
            notes = "회원의 읽지 않은 알림 개수를 조회합니다."
    )
    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount(
            @ApiParam(value = "인증된 사용자 정보", required = true) Principal principal
    ) {
        String email = String.valueOf(principal.getName());
        Long memberId = memberMapper.findIdByUsername(email);
        long unreadCount = notificationService.getUnreadCount(memberId);

        return ApiResponse.success(ResponseCode.NOTIFICATION_LIST_SUCCESS, unreadCount);
    }

    @ApiOperation(
            value = "특정 알림 읽음 처리",
            notes = "알림 ID에 해당하는 알림을 읽음 처리합니다."
    )
    @PutMapping("/{notificationId}/read")
    public ApiResponse<?> markAsRead(
            @ApiParam(value = "알림 ID", required = true) @PathVariable Long notificationId,
            @ApiParam(value = "인증된 사용자 정보", required = true) Principal principal
    ) {
        String email = String.valueOf(principal.getName());
        Long memberId = memberMapper.findIdByUsername(email);
        notificationService.markAsRead(notificationId, memberId);

        return ApiResponse.success(ResponseCode.NOTIFICATION_READ_SUCCESS);
    }

    @ApiOperation(
            value = "모든 알림 읽음 처리",
            notes = "회원의 모든 알림을 읽음 처리합니다."
    )
    @PutMapping("/read-all")
    public ApiResponse<?> markAllAsRead(
            @ApiParam(value = "인증된 사용자 정보", required = true) Principal principal
    ) {
        String email = String.valueOf(principal.getName());
        Long memberId = memberMapper.findIdByUsername(email);
        notificationService.markAllAsRead(memberId);

        return ApiResponse.success(ResponseCode.NOTIFICATION_READ_ALL_SUCCESS);
    }

    @ApiOperation(
            value = "알림 설정 조회",
            notes = "회원의 알림 설정 정보를 조회합니다."
    )
    @GetMapping("/settings")
    public ApiResponse<?> getNotificationSettings(
            @ApiParam(value = "인증된 사용자 정보", required = true) Principal principal
    ) {
        String email = String.valueOf(principal.getName());
        Long memberId = memberMapper.findIdByUsername(email);
        boolean settings = notificationService.getNotificationSettings(memberId);

        return ApiResponse.success(ResponseCode.NOTIFICATION_SETTINGS_GET_SUCCESS, settings);
    }

    @ApiOperation(
            value = "알림 설정 업데이트",
            notes = "회원의 알림 설정 정보를 수정합니다."
    )
    @PutMapping("/settings")
    public ApiResponse<?> updateNotificationSettings(
            @ApiParam(value = "알림 설정 수정 요청 정보", required = true) @RequestBody NotificationSettingUpdateRequest request,
            @ApiParam(value = "인증된 사용자 정보", required = true) Principal principal
    ) {
        String email = String.valueOf(principal.getName());
        Long memberId = memberMapper.findIdByUsername(email);
        notificationService.updateNotificationSettings(memberId, request);

        return ApiResponse.success(ResponseCode.NOTIFICATION_SETTINGS_UPDATE_SUCCESS);
    }
}