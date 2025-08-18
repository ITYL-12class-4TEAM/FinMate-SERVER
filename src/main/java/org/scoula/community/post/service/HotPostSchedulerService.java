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
import org.scoula.community.scrap.mapper.ScrapMapper;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.notification.helper.NotificationHelper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final ScrapMapper scrapMapper;
    private final MemberMapper memberMapper;
    private final NotificationHelper notificationHelper;

    private static final String HOT_POSTS_ALL_KEY = "hot_posts:all";
    private static final String HOT_POSTS_BOARD_KEY_PREFIX = "hot_posts:board:";
    private static final int CACHE_DURATION_DAYS = 1;

    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void updateHotPosts() {
        log.info("핫게시물 업데이트 스케줄러 시작...");
        try {
            executeHotPostUpdate();
        } catch (Exception e) {
            log.error("핫게시물 업데이트 실패", e);
        }

        try {
            sendHotPostNotifications();
        } catch (Exception e) {
            log.error("핫게시물 알림 발송 실패", e);
        }

    }

    // 테스트용 메서드 - 현재 시각에 실행
    public void updateHotPostsNow() {
        log.info("핫게시물 업데이트 즉시 실행...");
        executeHotPostUpdate();
        sendHotPostNotifications();
    }

    @Transactional(readOnly = true)
    public void executeHotPostUpdate() {
        updateAllHotPosts();
        updateHotPostsByBoard();
        log.info("핫게시물 업데이트 완료");

    }
    private void sendHotPostNotifications() {
        try {
            // 오늘의 핫게시물 조회
            List<PostVO> todayHotPosts = postMapper.getAllHotPosts();

            for (PostVO post : todayHotPosts) {
                String notificationKey = "hot_post_notified:" + post.getPostId();
                if (redisObjectTemplate.hasKey(notificationKey)) {
                    continue;
                }

                notificationHelper.notifyHotPost(
                        post.getPostId(),
                        post.getTitle()
                );

                redisObjectTemplate.opsForValue().set(
                        notificationKey,
                        "notified",
                        Duration.ofHours(24)
                );
            }

            log.info("핫게시물 알림 발송 완료: {} 개 게시물", todayHotPosts.size());

        } catch (Exception e) {
            log.error("핫게시물 알림 발송 실패", e);
        }
    }

    private void updateAllHotPosts() {
        try {
            List<PostVO> hotPosts = postMapper.getAllHotPosts();

            if (hotPosts.isEmpty()) {
                log.info("핫게시물이 없습니다.");
                redisObjectTemplate.opsForValue().set(HOT_POSTS_ALL_KEY, List.of(), Duration.ofDays(CACHE_DURATION_DAYS));
                return;
            }

            enrichPostsWithCounts(hotPosts);

            Long currentUserId = getCurrentUserIdAsLong(); // 혹은 서비스 레벨에서 특정 사용자 기준이 있으면 넣기, 없으면 null 처리

            List<PostListResponseDTO> hotPostDTOs = hotPosts.stream()
                    .map(post -> {
                        int likeCount = postLikeMapper.countByPostId(post.getPostId());
                        int commentCount = postMapper.countCommentsByPostId(post.getPostId());
                        int scrapCount = scrapMapper.countScrapsByPostId(post.getPostId());

                        post.setLikeCount(likeCount);
                        post.setCommentCount(commentCount);
                        post.setScrapCount(scrapCount); // scrapCount 필드 및 메서드 필요

                        boolean isLiked = false;
                        boolean isScraped = false;
                        if (currentUserId != null) {
                            isLiked = postLikeMapper.existsByPostIdAndMemberId(post.getPostId(), currentUserId);
                            isScraped = scrapMapper.existsScrap(post.getPostId(), currentUserId);
                        }
                        post.setLiked(isLiked);
                        post.setScraped(isScraped);
                        String  nickname = memberMapper.getNicknameByMemberId(currentUserId);
                        return PostListResponseDTO.of(post, nickname);
                    })
                    .limit(5)
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
        Long currentUserId = getCurrentUserIdAsLong();  // 현재 로그인 사용자 ID 조회

        List<PostVO> hotPosts = postMapper.getHotPostsByBoard(boardId);

        if (hotPosts.isEmpty()) {
            log.info("게시판 {}에 핫게시물이 없습니다.", boardId);
            String key = HOT_POSTS_BOARD_KEY_PREFIX + boardId;
            redisObjectTemplate.opsForValue().set(key, List.of(), Duration.ofDays(CACHE_DURATION_DAYS));
            return;
        }

        enrichPostsWithCounts(hotPosts);

        List<PostListResponseDTO> hotPostDTOs = hotPosts.stream()
                .map(post -> {
                    int likeCount = postLikeMapper.countByPostId(post.getPostId());
                    int commentCount = postMapper.countCommentsByPostId(post.getPostId());
                    int scrapCount = scrapMapper.countScrapsByPostId(post.getPostId());

                    post.setLikeCount(likeCount);
                    post.setCommentCount(commentCount);
                    post.setScrapCount(scrapCount); // scrapCount 필드 및 메서드 필요

                    boolean isLiked = false;
                    boolean isScraped = false;
                    if (currentUserId != null) {
                        isLiked = postLikeMapper.existsByPostIdAndMemberId(post.getPostId(), currentUserId);
                        isScraped = scrapMapper.existsScrap(post.getPostId(), currentUserId);
                    }
                    post.setLiked(isLiked);
                    post.setScraped(isScraped);
                    String  nickname = memberMapper.getNicknameByMemberId(currentUserId);
                    return PostListResponseDTO.of(post, nickname);
                })
                .limit(5)
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

    private Long getCurrentUserIdAsLong() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberMapper.getMemberIdByEmail(email); // 👈 이메일로 memberId 조회하는 쿼리 필요
    }
}