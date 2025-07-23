package org.scoula.community.board.domain;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardVO {

    private Long boardId;
    private String title;
    private Long memberId; // Member로 연관관계
    private BoardType type;
    private Date createdAt;
    private Date updatedAt;
}
