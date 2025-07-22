package org.scoula.community.board.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.community.board.domain.BoardType;
import org.scoula.community.board.domain.BoardVO;
import org.scoula.community.board.dto.BoardDTO;
import org.scoula.community.board.mapper.BoardMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Log4j2
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardMapper boardMapper;

    @Override
    public BoardDTO create(BoardDTO boardDTO) {
        BoardVO vo = boardDTO.toVo();
        boardMapper.create(vo);
        boardDTO.setNo(vo.getNo());
        return get(vo.getNo());
    }

    @Override
    public List<BoardDTO> getList() {
        log.info("getList..........");
        List<BoardVO> boardVOList = boardMapper.getList();
        boardVOList.forEach(vo -> log.info("BoardVO: " + vo));

        return boardVOList.stream()
                .map(BoardDTO::of)
                .toList();
    }


    private BoardDTO get(Long no) {
        log.info("get..........");
        BoardDTO boardDTO = BoardDTO.of(boardMapper.get(no));
        return Optional.ofNullable(boardDTO).orElseThrow(NoSuchElementException::new);
    }
}
