package org.scoula.community.postlike.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLikeVO {
    private Long postLikeId;
    private Long postId;
    private Long memberId;
    private boolean isLiked;
    private LocalDateTime createdAt;
}
