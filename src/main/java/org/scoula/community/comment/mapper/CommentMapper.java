package org.scoula.community.comment.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.scoula.community.comment.domain.CommentVO;

@Mapper
public interface CommentMapper {
    public List<CommentVO> getList();
    public CommentVO get(Long no);
    public void create(CommentVO comment);
    int deleteChild(Long commentId);
    int deleteParentAndChildren(Long commentId);
    boolean existsById(Long commentId);
    void updateLikeCount(Long commentId);
    int countAllByParentOrSelf(Long commentId);
}
