package org.scoula.community.comment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class CommentResponseDTO {
    private Long commentId;
    private Long postId;
    private Long memberId;
    private String content;
    private boolean isAnonymous;
    private Long parentComment;
    private LocalDateTime createdAt;
    @ApiModelProperty(value = "좋아요 수", example = "15", position = 11)
    private int likeCount;

    public static CommentResponseDTO of(CommentVO vo) {
        return vo == null ? null : CommentResponseDTO.builder()
                .commentId(vo.getCommentId())
                .postId(vo.getPostId())
                .memberId(vo.getMemberId())
                .content(vo.getContent())
                .isAnonymous(vo.isAnonymous())
                .parentComment(vo.getParentComment())
                .createdAt(vo.getCreatedAt())
                .likeCount(vo.getLikeCount())
                .build();
    }

    public CommentVO toVo() {
        return CommentVO.builder()
                .commentId(commentId)
                .postId(postId)
                .memberId(memberId)
                .content(content)
                .isAnonymous(isAnonymous)
                .parentComment(parentComment)
                .createdAt(createdAt)
                .likeCount(likeCount)
                .build();
    }
}
