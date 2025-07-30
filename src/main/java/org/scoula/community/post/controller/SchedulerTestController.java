package org.scoula.community.post.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.community.post.service.HotPostSchedulerService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/admin/scheduler")
@RequiredArgsConstructor
@Log4j2
@Api(tags = "스케줄러 테스트 API")
//@Profile({"dev", "local"})
public class SchedulerTestController {

    private final HotPostSchedulerService hotPostSchedulerService;

    @ApiOperation(value = "핫게시물 즉시 업데이트",
            notes = "스케줄러를 기다리지 않고 현재 시각에 핫게시물을 업데이트합니다.")
    @PostMapping("/hot-posts/update")
    public ApiResponse<String> updateHotPostsNow() {
        try {
            hotPostSchedulerService.updateHotPostsNow();
            return ApiResponse.success(ResponseCode.HOT_POSTS_UPDATE_SUCCESS);
        } catch (Exception e) {
            log.error("핫게시물 즉시 업데이트 실패", e);
            return ApiResponse.fail(ResponseCode.HOT_POSTS_UPDATE_FAIL);
        }
    }

    @ApiOperation(value = "핫게시물 캐시 삭제",
            notes = "Redis에 저장된 핫게시물 캐시를 삭제합니다.")
    @PostMapping("/hot-posts/clear-cache")
    public ApiResponse<String> clearHotPostsCache() {
        try {
            hotPostSchedulerService.clearHotPostsCache();
            return ApiResponse.success(ResponseCode.HOT_POSTS_CACHE_CLEAR_SUCCESS);
        } catch (Exception e) {
            log.error("핫게시물 캐시 삭제 실패", e);
            return ApiResponse.fail(ResponseCode.HOT_POSTS_CACHE_CLEAR_FAIL);
        }
    }
}