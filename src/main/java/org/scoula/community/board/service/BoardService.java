package org.scoula.community.board.service;

import java.util.List;
import org.scoula.community.board.domain.BoardType;
import org.scoula.community.board.dto.BoardDTO;

public interface BoardService {
    public BoardDTO create(BoardDTO boardDTO);
    public List<BoardDTO> getList();
}
