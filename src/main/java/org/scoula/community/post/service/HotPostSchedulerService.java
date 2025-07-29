package org.scoula.community.post.service;

import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.community.board.mapper.BoardMapper;
import org.scoula.community.post.domain.PostVO;
import org.scoula.community.post.dto.PostListResponseDTO;
import org.scoula.community.post.mapper.PostMapper;
import org.scoula.community.postlike.mapper.PostLikeMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
public class HotPostSchedulerService {

    private final PostMapper postMapper;
    private final PostLikeMapper postLikeMapper;
    private final RedisTemplate<String, Object> redisObjectTemplate;
    private final BoardMapper boardMapper;

    private static final String HOT_POSTS_ALL_KEY = "hot_posts:all";
    private static final String HOT_POSTS_BOARD_KEY_PREFIX = "hot_posts:board:";
    private static final int CACHE_DURATION_DAYS = 1;

    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void updateHotPosts() {
        log.info("핫게시물 업데이트 스케줄러 시작...");
        executeHotPostUpdate();
    }

    // 테스트용 메서드 - 현재 시각에 실행
    public void updateHotPostsNow() {
        log.info("핫게시물 업데이트 즉시 실행...");
        executeHotPostUpdate();
    }

    @Transactional(readOnly = true)
    private void executeHotPostUpdate() {
        try {
            updateAllHotPosts();
            updateHotPostsByBoard();
            log.info("핫게시물 업데이트 완료");
        } catch (Exception e) {
            log.error("핫게시물 업데이트 실패", e);
            // 운영환경에서는 알림 시스템 연동 고려
            throw new RuntimeException("핫게시물 업데이트 실패", e);
        }
    }

    private void updateAllHotPosts() {
        try {
            List<PostVO> hotPosts = postMapper.getAllHotPosts();

            if (hotPosts.isEmpty()) {
                log.info("핫게시물이 없습니다.");
                // 빈 리스트라도 캐시에 저장하여 불필요한 DB 조회 방지
                redisObjectTemplate.opsForValue().set(HOT_POSTS_ALL_KEY, List.of(), Duration.ofDays(CACHE_DURATION_DAYS));
                return;
            }

            enrichPostsWithCounts(hotPosts);

            List<PostListResponseDTO> hotPostDTOs = hotPosts.stream()
                    .map(PostListResponseDTO::of)
                    .limit(5) // 상위 5개로 제한
                    .toList();

            redisObjectTemplate.opsForValue().set(HOT_POSTS_ALL_KEY, hotPostDTOs, Duration.ofDays(CACHE_DURATION_DAYS));
            log.info("전체 핫게시물 {} 개 Redis에 저장 완료", hotPostDTOs.size());
        } catch (Exception e) {
            log.error("전체 핫게시물 업데이트 실패", e);
            throw e;
        }
    }

    private void updateHotPostsByBoard() {
        List<Long> boardIds = getBoardIds();

        for (Long boardId : boardIds) {
            try {
                updateHotPostsForBoard(boardId);
            } catch (Exception e) {
                log.error("게시판 {} 핫게시물 업데이트 실패", boardId, e);
                // 한 게시판 실패가 다른 게시판 업데이트를 막지 않도록 continue
            }
        }
    }

    private void updateHotPostsForBoard(Long boardId) {
        List<PostVO> hotPosts = postMapper.getHotPostsByBoard(boardId);

        if (hotPosts.isEmpty()) {
            log.info("게시판 {}에 핫게시물이 없습니다.", boardId);
            String key = HOT_POSTS_BOARD_KEY_PREFIX + boardId;
            redisObjectTemplate.opsForValue().set(key, List.of(), Duration.ofDays(CACHE_DURATION_DAYS));
            return;
        }

        enrichPostsWithCounts(hotPosts);

        List<PostListResponseDTO> hotPostDTOs = hotPosts.stream()
                .map(PostListResponseDTO::of)
                .limit(5) // 상위 5개로 제한
                .toList();

        String key = HOT_POSTS_BOARD_KEY_PREFIX + boardId;
        redisObjectTemplate.opsForValue().set(key, hotPostDTOs, Duration.ofDays(CACHE_DURATION_DAYS));
        log.info("게시판 {} 핫게시물 {} 개 Redis에 저장 완료", boardId, hotPostDTOs.size());
    }

    private void enrichPostsWithCounts(List<PostVO> posts) {
        for (PostVO post : posts) {
            try {
                int commentCount = postMapper.countCommentsByPostId(post.getPostId());
                post.setCommentCount(commentCount);

                int likeCount = postLikeMapper.countByPostId(post.getPostId());
                post.setLikeCount(likeCount);
            } catch (Exception e) {
                log.warn("게시물 {} 카운트 조회 실패", post.getPostId(), e);
                // 기본값 설정
                post.setCommentCount(0);
                post.setLikeCount(0);
            }
        }
    }

    private List<Long> getBoardIds() {
        try {
            List<Long> boardIds = boardMapper.getAllBoardIds();
            log.info("데이터베이스에서 게시판 ID 조회: {}", boardIds);
            return boardIds;
        } catch (Exception e) {
            log.warn("게시판 ID 조회 실패, 기본값 사용: {}", e.getMessage());
            return List.of(1L, 2L, 3L); // 기본값
        }
    }

    // Redis 캐시 수동 삭제 메서드 (관리용)
    public void clearHotPostsCache() {
        try {
            // 전체 핫게시물 캐시 삭제
            redisObjectTemplate.delete(HOT_POSTS_ALL_KEY);

            // 모든 게시판 핫게시물 캐시 삭제
            List<Long> boardIds = getBoardIds();
            for (Long boardId : boardIds) {
                String key = HOT_POSTS_BOARD_KEY_PREFIX + boardId;
                redisObjectTemplate.delete(key);
            }

            log.info("핫게시물 캐시 삭제 완료");
        } catch (Exception e) {
            log.error("핫게시물 캐시 삭제 실패", e);
        }
    }
}