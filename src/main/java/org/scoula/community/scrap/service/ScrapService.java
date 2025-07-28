package org.scoula.community.scrap.service;

import java.util.List;
import org.scoula.community.post.dto.PostListResponseDTO;
import org.scoula.community.scrap.dto.ScrapCountResponseDTO;
import org.scoula.community.scrap.dto.ScrapResponseDTO;

public interface ScrapService {
    ScrapResponseDTO toggleScrap(Long postId);
    boolean isScraped(Long postId, Long memberId);
    List<PostListResponseDTO> getMyScrapList();
    ScrapCountResponseDTO getScrapCount(Long postId);
    void deleteScrapsByPostId(Long postId); // 게시글 삭제시 사용
}