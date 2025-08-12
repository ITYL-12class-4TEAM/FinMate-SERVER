package org.scoula.community.commentlike.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.community.commentlike.dto.CommentLikeToggleResponseDTO;
import org.scoula.community.commentlike.service.CommentLikeService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comment-like")
@RequiredArgsConstructor
@Log4j2
@Api(tags = "댓글 좋아요 API")
public class CommentLikeApiController {

    private final CommentLikeService commentLikeService;

    @PostMapping("/{commentId}/toggle")
    @ApiOperation("댓글 좋아요 토글")
    public ApiResponse<CommentLikeToggleResponseDTO> toggleLike(@PathVariable("commentId") Long commentId) {
        log.info("Toggle like - commentId: {}, memberId: {}", commentId);
        boolean liked = commentLikeService.toggleLike(commentId);
        return ApiResponse.success(
                liked ? ResponseCode.COMMENT_LIKE_CREATE_SUCCESS : ResponseCode.COMMENT_LIKE_CANCEL_SUCCESS,
                new CommentLikeToggleResponseDTO(liked)
        );
    }

    @GetMapping("/{commentId}/count")
    @ApiOperation("댓글 좋아요 개수 조회")
    public ApiResponse<Integer> getLikeCount(@PathVariable Long commentId) {
        int response = commentLikeService.getLikeCount(commentId);
        return ApiResponse.success(ResponseCode.COMMENT_LIKE_COUNT_SUCCESS, response);
    }

    @GetMapping("/{commentId}/me")
    @ApiOperation("해당 댓글 좋아요 여부 조회")
    public ApiResponse<CommentLikeToggleResponseDTO> getMyLikeStatus(@PathVariable Long commentId,
                                          @RequestParam("memberId") Long memberId) {
        boolean liked = commentLikeService.isLikedByMember(commentId, memberId);
        return ApiResponse.success(ResponseCode.COMMENT_LIKE_STATUS_SUCCESS, new CommentLikeToggleResponseDTO(liked));
    }
}