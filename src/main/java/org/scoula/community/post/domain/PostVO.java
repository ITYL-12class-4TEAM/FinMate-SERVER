package org.scoula.community.post.domain;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.community.comment.domain.CommentVO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostVO {
    private Long postId;
    private Long boardId;
    private Long memberId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private boolean isAnonymous;
    private int likeCount;
    private int commentCount;
    private PostStatus status;
    private ProductTag productTag;
    private List<PostAttachmentVO> attachments;

}
