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

    @ApiModelProperty(value = "게시글 ID", example = "1", required = true)
    private Long postId;

    @ApiModelProperty(value = "회원 ID", example = "1", required = true)
    private Long memberId;

    @ApiModelProperty(value = "댓글 내용", example = "이 글 정말 좋아요!", required = true)
    private String content;

    @ApiModelProperty(value = "익명 여부", example = "false", required = true)
    private boolean isAnonymous;

    @ApiModelProperty(value = "부모 댓글 ID (답글일 경우). " +
            "null이면 최상위 댓글. " +
            "부모 댓글이 존재한다면 해당 댓글이 존재하고 게시글이 일치하는지 검증 필요.", example = "null")
    private Long parentComment;


    public static CommentCreateRequestDTO of(CommentVO vo) {
        return vo == null ? null : CommentCreateRequestDTO.builder()
                .postId(vo.getPostId())
                .memberId(vo.getMemberId())
                .content(vo.getContent())
                .isAnonymous(vo.isAnonymous())
                .parentComment(vo.getParentComment())
                .build();
    }

    public CommentVO toVo() {
        return CommentVO.builder()
                .postId(postId)
                .memberId(memberId)
                .content(content)
                .isAnonymous(isAnonymous)
                .parentComment((parentComment != null && parentComment == 0) ? null : parentComment)
                .build();
    }
}
