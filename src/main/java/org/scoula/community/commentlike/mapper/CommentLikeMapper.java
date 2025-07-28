package org.scoula.community.commentlike.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.community.commentlike.domain.CommentLikeVO;

@Mapper
public interface CommentLikeMapper {
    CommentLikeVO findByCommentIdAndMemberId(@Param("commentId") Long commentId, @Param("memberId") Long memberId);
    void insert(CommentLikeVO commentLike);
    void update(CommentLikeVO commentLike);
    int countByCommentId(@Param("commentId") Long commentId);
    void deleteByCommentIdAndMemberId(@Param("commentId") Long commentId, @Param("memberId") Long memberId);
}
