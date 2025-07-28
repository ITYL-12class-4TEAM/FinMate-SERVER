package org.scoula.community.commentlike.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentLikeVO {
    private Long commentLikeId;
    private Long commentId;
    private Long memberId;
    private boolean isLiked;
    private LocalDateTime createdAt;
}
