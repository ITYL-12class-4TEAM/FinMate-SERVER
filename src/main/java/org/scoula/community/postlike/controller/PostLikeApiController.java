package org.scoula.community.postlike.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.community.postlike.dto.PostLikeToggleResponseDTO;
import org.scoula.community.postlike.service.PostLikeService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/post-like")
@RequiredArgsConstructor
@Log4j2
@Api(tags = "게시글 좋아요 API")
public class PostLikeApiController {

    private final PostLikeService postLikeService;

    @PostMapping("/{postId}/toggle")
    @ApiOperation("게시글 좋아요 토글")
    public ApiResponse<?> toggleLike(@PathVariable("postId") Long postId,
                                     @RequestParam("memberId") Long memberId) {
        log.info("Toggle like - postId: {}, memberId: {}", postId, memberId);
        boolean liked = postLikeService.toggleLike(postId, memberId);
        return ApiResponse.success(
                liked ? ResponseCode.POST_LIKE_CREATE_SUCCESS : ResponseCode.POST_LIKE_CANCEL_SUCCESS,
                new PostLikeToggleResponseDTO(liked)
        );
    }

    @GetMapping("/{postId}/count")
    @ApiOperation("게시글 좋아요 개수 조회")
    public ApiResponse<?> getLikeCount(@PathVariable Long postId) {
        return ApiResponse.success(ResponseCode.POST_LIKE_COUNT_SUCCESS, postLikeService.getLikeCount(postId));
    }
}