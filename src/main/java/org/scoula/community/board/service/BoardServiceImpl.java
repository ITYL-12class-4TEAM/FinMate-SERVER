package org.scoula.community.board.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.community.board.domain.BoardVO;
import org.scoula.community.board.dto.BoardDTO;
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
    public BoardDTO create(BoardDTO boardDTO) {
        BoardVO vo = boardDTO.toVo();
        boardMapper.create(vo);
        boardDTO.setBoardId(vo.getBoardId());
        return get(vo.getBoardId());
    }

    @Override
    public List<BoardDTO> getList() {
        log.info("getList..........");
        List<BoardVO> boardVOList = boardMapper.getList();

        return boardVOList.stream()
                .map(BoardDTO::of)
                .toList();
    }


    private BoardDTO get(Long boardId) {
        log.info("get..........");
        BoardVO boardVO = boardMapper.get(boardId);
        if (boardVO == null) {
            throw new BoardNotFoundException(ResponseCode.BOARD_NOT_FOUND);
        }
        return BoardDTO.of(boardVO);
    }
}
