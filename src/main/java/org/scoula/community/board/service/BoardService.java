package org.scoula.community.board.service;

import java.util.List;
import org.scoula.community.board.dto.BoardResponseDTO;

public interface BoardService {
    public BoardResponseDTO create(BoardResponseDTO boardResponseDTO);
    public List<BoardResponseDTO> getList();
}
