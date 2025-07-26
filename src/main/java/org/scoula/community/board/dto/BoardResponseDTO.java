package org.scoula.community.board.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.community.board.domain.BoardType;
import org.scoula.community.board.domain.BoardVO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoardResponseDTO {
    private Long boardId;
    private String title;
    private Long memberId;
    private String boardType;
    private Date createdAt;

    public static BoardResponseDTO of(BoardVO vo) {
        System.out.println("vo.getType() = " + vo.getType());
        return vo == null ? null : BoardResponseDTO.builder()
                .boardId(vo.getBoardId())
                .title(vo.getTitle())
                .memberId(vo.getMemberId())
                .boardType(vo.getType() != null ? vo.getType().getCode() : BoardType.자유게시판.getCode())  // 기본값 설정
                .createdAt(vo.getCreatedAt())
                .build();
    }

    public BoardVO toVo() {
        BoardType boardTypeEnum = BoardType.fromCode(boardType);
        System.out.println("boardTypeEnum = " + boardTypeEnum);
        return BoardVO.builder()
                .boardId(boardId)
                .title(title)
                .memberId(memberId)
                .type(boardTypeEnum)
                .createdAt(createdAt)
                .build();
    }
}
