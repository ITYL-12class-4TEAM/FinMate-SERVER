package org.scoula.community.scrap.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.community.post.exception.PostNotFoundException;
import org.scoula.community.post.mapper.PostMapper;
import org.scoula.community.scrap.domain.PostScrapVO;
import org.scoula.community.post.dto.PostListResponseDTO;
import org.scoula.community.scrap.dto.ScrapCountResponseDTO;
import org.scoula.community.scrap.dto.ScrapResponseDTO;
import org.scoula.community.scrap.mapper.ScrapMapper;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.response.ResponseCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
public class ScrapServiceImpl implements ScrapService {

    private final ScrapMapper scrapMapper;
    private final MemberMapper memberMapper;
    private final PostMapper postMapper;

    @Override
    @Transactional
    public ScrapResponseDTO toggleScrap(Long postId) {
        validatePostExists(postId);
        Long memberId = getCurrentUserIdAsLong();
        boolean isScraped;

        if (scrapMapper.existsScrap(postId, memberId)) {
            // 스크랩 해제
            scrapMapper.deleteScrap(postId, memberId);
            isScraped = false;
            log.info("스크랩 해제: postId={}, memberId={}", postId, memberId);
        } else {
            // 스크랩 추가
            PostScrapVO scrap = PostScrapVO.builder()
                    .postId(postId)
                    .memberId(memberId)
                    .build();
            scrapMapper.createScrap(scrap);
            isScraped = true;
            log.info("스크랩 추가: postId={}, memberId={}", postId, memberId);
        }

        // 현재 스크랩 수 조회
        int scrapCount = scrapMapper.countScrapsByPostId(postId);

        return ScrapResponseDTO.of(postId, isScraped, scrapCount);
    }

    @Override
    public boolean isScraped(Long postId, Long memberId) {
        validatePostExists(postId);
        return scrapMapper.existsScrap(postId, memberId);
    }

    @Override
    public List<PostListResponseDTO> getMyScrapList() {
        Long memberId = getCurrentUserIdAsLong();
        return scrapMapper.getScrapPostsByMemberId(memberId).stream()
                .map(PostListResponseDTO::of)
                .toList();
    }

    @Override
    public ScrapCountResponseDTO getScrapCount(Long postId) {
        validatePostExists(postId);
        int scrapCount = scrapMapper.countScrapsByPostId(postId);
        return ScrapCountResponseDTO.of(postId, scrapCount);
    }

    @Override
    @Transactional
    public void deleteScrapsByPostId(Long postId) {
        scrapMapper.deleteScrapsByPostId(postId);
        log.info("게시글 스크랩 모두 삭제: postId={}", postId);
    }

    private Long getCurrentUserIdAsLong() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberMapper.getMemberIdByEmail(email);
    }
    private void validatePostExists(Long postId) {
        if (!postMapper.existsById(postId)) {
            throw new PostNotFoundException(ResponseCode.POST_NOT_FOUND);
        }
    }
}