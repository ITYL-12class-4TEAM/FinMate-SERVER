package org.scoula.community.postlike.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.community.post.domain.PostVO;
import org.scoula.community.postlike.domain.PostLikeVO;

@Mapper
public interface PostLikeMapper {
    PostLikeVO findByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);
    void insert(PostLikeVO postLike);
    void update(PostLikeVO postLike);
    int countByPostId(@Param("postId") Long postId);
    void deleteByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);
    List<PostVO> getLikedPostsByMemberId(Long memberId);
}
