package org.scoula.community.comment.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.auth.exception.AccessDeniedException;
import org.scoula.community.comment.dto.CommentCreateRequestDTO;
import org.scoula.community.comment.dto.CommentResponseDTO;
import org.scoula.community.comment.service.CommentService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Log4j2
@Api(tags = "댓글 API")
public class CommentApiController {

    private final CommentService commentService;

    @ApiOperation(value = "게시글에 해당하는 댓글 리스트 조회", notes = "특정 게시글의 모든 댓글을 조회합니다.")
    @GetMapping("/post/{postId}")
    public ApiResponse<List<CommentResponseDTO>> getListByPostId(@PathVariable Long postId) {
        return ApiResponse.success(ResponseCode.COMMENT_LIST_SUCCESS, commentService.getListByPostId(postId));
    }

    @ApiOperation(value = "댓글 단건 조회", notes = "commentId에 해당하는 댓글 상세 정보를 반환합니다.")
    @GetMapping("/{commentId}")
    public ApiResponse<CommentResponseDTO> get(@PathVariable Long commentId) {
        return ApiResponse.success(ResponseCode.COMMENT_DETAILS_SUCCESS, commentService.get(commentId));
    }

    @ApiOperation(value = "부모 댓글과 그에 해당하는 대댓글 조회", notes = "부모 댓글과 그에 해당하는 대댓글을 조회합니다.")
    @GetMapping("/parent/{parentCommentId}")
    public ApiResponse<List<CommentResponseDTO>> getParentAndReplies(@PathVariable Long parentCommentId) {
        return ApiResponse.success(ResponseCode.COMMENT_LIST_SUCCESS, commentService.getParentAndReplies(parentCommentId));
    }

    @ApiOperation(value = "댓글 생성", notes = "새 댓글을 등록합니다.")
    @PostMapping("")
    public ApiResponse<CommentResponseDTO> create(
            @RequestBody CommentCreateRequestDTO commentCreateRequestDTO, @AuthenticationPrincipal UserDetails user) {
        if (user == null) {
            throw new AccessDeniedException(ResponseCode.UNAUTHORIZED_USER);
        }
        CommentResponseDTO created = commentService.create(commentCreateRequestDTO);
        return ApiResponse.success(ResponseCode.COMMENT_CREATE_SUCCESS, created);
    }

    @ApiOperation(value = "댓글 삭제", notes = "commentId에 해당하는 댓글을 삭제합니다.")
    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> delete(@PathVariable Long commentId, @AuthenticationPrincipal UserDetails user) {
        if (user == null) {
            throw new AccessDeniedException(ResponseCode.UNAUTHORIZED_USER);
        }
        commentService.delete(commentId);
        return ApiResponse.success(ResponseCode.COMMENT_DELETE_SUCCESS);
    }

    @ApiOperation(value = "내가 쓴 댓글 조회", notes = "현재 로그인한 사용자가 작성한 댓글 목록을 조회합니다.")
    @GetMapping("/my")
    public ApiResponse<List<CommentResponseDTO>> getMyComments(@AuthenticationPrincipal UserDetails user) {
        if (user == null) {
            throw new AccessDeniedException(ResponseCode.UNAUTHORIZED_USER);
        }
        return ApiResponse.success(ResponseCode.COMMENT_LIST_SUCCESS,  commentService.getMyComments());
    }
}
