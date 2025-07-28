package org.scoula.community.board.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.community.board.domain.BoardVO;
import org.scoula.community.board.dto.BoardResponseDTO;
import org.scoula.community.board.exception.BoardNotFoundException;
import org.scoula.community.board.mapper.BoardMapper;
import org.scoula.response.ResponseCode;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardMapper boardMapper;

    @Override
    public BoardResponseDTO create(BoardResponseDTO boardResponseDTO) {
        BoardVO vo = boardResponseDTO.toVo();
        boardMapper.create(vo);
        boardResponseDTO.setBoardId(vo.getBoardId());
        return get(vo.getBoardId());
    }

    @Override
    public List<BoardResponseDTO> getList() {
        log.info("getList..........");
        List<BoardVO> boardVOList = boardMapper.getList();

        return boardVOList.stream()
                .map(BoardResponseDTO::of)
                .toList();
    }


    private BoardResponseDTO get(Long boardId) {
        log.info("get..........");
        BoardVO boardVO = boardMapper.get(boardId);
        if (boardVO == null) {
            throw new BoardNotFoundException(ResponseCode.BOARD_NOT_FOUND);
        }
        return BoardResponseDTO.of(boardVO);
    }
}
