package org.scoula.community.comment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.community.comment.domain.CommentVO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "댓글 생성 요청 DTO")
public class CommentCreateRequestDTO {

    @ApiModelProperty(value = "댓글 ID", example = "1", required = false)
    private Long commentId;

    @ApiModelProperty(value = "게시글 ID", example = "123", required = true)
    private Long postId;

    @ApiModelProperty(value = "회원 ID", example = "456", required = true)
    private Long memberId;

    @ApiModelProperty(value = "댓글 내용", example = "이 글 정말 좋아요!", required = true)
    private String content;

    @ApiModelProperty(value = "익명 여부", example = "false", required = true)
    private boolean isAnonymous;

    @ApiModelProperty(value = "부모 댓글 ID (답글일 경우)", example = "1")
    private Long parentComment;

    @ApiModelProperty(value = "댓글 생성 시간", example = "2025-07-23T10:00:00")
    private LocalDateTime createdAt;

    public static CommentCreateRequestDTO of(CommentVO vo) {
        return vo == null ? null : CommentCreateRequestDTO.builder()
                .commentId(vo.getCommentId())
                .postId(vo.getPostId())
                .memberId(vo.getMemberId())
                .content(vo.getContent())
                .isAnonymous(vo.isAnonymous())
                .parentComment(vo.getParentComment())
                .createdAt(vo.getCreatedAt())
                .build();
    }

    public CommentVO toVo() {
        return CommentVO.builder()
                .commentId(commentId)
                .postId(postId)
                .memberId(memberId)
                .content(content)
                .isAnonymous(isAnonymous)
                .parentComment((parentComment != null && parentComment == 0) ? null : parentComment)
                .createdAt(createdAt)
                .build();
    }
}
