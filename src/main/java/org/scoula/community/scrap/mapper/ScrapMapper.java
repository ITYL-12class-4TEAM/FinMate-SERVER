package org.scoula.community.scrap.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.community.scrap.domain.PostScrapVO;
import org.scoula.community.post.domain.PostVO;

@Mapper
public interface ScrapMapper {
    void createScrap(PostScrapVO scrap);
    boolean existsScrap(@Param("postId") Long postId, @Param("memberId") Long memberId);
    void deleteScrap(@Param("postId") Long postId, @Param("memberId") Long memberId);
    void deleteScrapsByPostId(Long postId);
    int countScrapsByPostId(Long postId);
    List<PostVO> getScrapPostsByMemberId(Long memberId);
}
