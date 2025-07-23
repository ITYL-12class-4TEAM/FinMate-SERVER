package org.scoula.community.post.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.scoula.community.board.domain.BoardVO;
import org.scoula.community.post.domain.PostAttachmentVO;
import org.scoula.community.post.domain.PostVO;


@Mapper
public interface PostMapper {
    public List<PostVO> getList();
    public PostVO get(Long no);
    public void create(PostVO board);
    public int update(PostVO board);
    public int delete(Long no);

    public void createAttachment(PostAttachmentVO attach);
    public List<PostAttachmentVO> getAttachmentList(Long bno);
    public PostAttachmentVO getAttachment(Long no);
    public int deleteAttachment(Long no);
}
