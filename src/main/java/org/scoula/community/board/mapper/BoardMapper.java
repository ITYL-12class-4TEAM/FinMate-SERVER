package org.scoula.community.board.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.scoula.community.board.domain.BoardType;
import org.scoula.community.board.domain.BoardVO;


@Mapper
public interface BoardMapper {
    public BoardVO get(Long no);
    public List<BoardVO> getList();
    public void create(BoardVO board);
}
