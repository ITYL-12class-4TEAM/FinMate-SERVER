package org.scoula.community.post.domain;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.community.board.domain.BoardVO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostVO {
    private Long postId;
    //연관관계 일대다
    private Long boardId;
    //나중에 연관관계 설정 예정
    private Long wmtiId;
    //나중에 연관관계 설정 예정
    private Long memberId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private LocalDateTime hotBoardTime;
    private boolean isAnonymous;
    private int likeCount;
    private int commentCount;
    private PostStatus status;

    private List<PostAttachmentVO> attaches;

}
