package org.scoula.community.comment.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentVO {
    private Long commentId;
    private Long postId;
    private Long memberId;
    private String content;
    private boolean isAnonymous;
    private Long parentComment;
    private LocalDateTime createdAt;
}
