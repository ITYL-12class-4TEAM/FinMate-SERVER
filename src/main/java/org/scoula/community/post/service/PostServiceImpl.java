package org.scoula.community.post.service;

import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.auth.exception.AccessDeniedException;
import org.scoula.community.comment.domain.CommentVO;
import org.scoula.community.post.domain.PostVO;
import org.scoula.community.post.domain.ProductTag;
import org.scoula.community.post.dto.PageRequestDTO;
import org.scoula.community.post.dto.PageResponseDTO;
import org.scoula.community.post.dto.PostCreateRequestDTO;
import org.scoula.community.post.dto.PostDetailsResponseDTO;
import org.scoula.community.post.dto.PostListResponseDTO;
import org.scoula.community.post.dto.PostUpdateRequestDTO;
import org.scoula.community.post.exception.InvalidTagException;
import org.scoula.community.post.exception.PostNotFoundException;
import org.scoula.community.post.mapper.PostMapper;
import org.scoula.community.postlike.mapper.PostLikeMapper;
import org.scoula.community.postlike.service.PostLikeService;
import org.scoula.community.scrap.mapper.ScrapMapper;
import org.scoula.community.scrap.service.ScrapService;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.response.ResponseCode;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    final private PostMapper postMapper;
    private final MemberMapper memberMapper;
    private final PostLikeMapper postLikeMapper;
    private final RedisTemplate<String, Object> redisObjectTemplate;
    private final ScrapMapper scrapMapper;

    private static final String HOT_POSTS_ALL_KEY = "hot_posts:all";
    private static final String HOT_POSTS_BOARD_KEY_PREFIX = "hot_posts:board:";

    @Override
    public List<PostListResponseDTO> getList() {
        log.info("getList..........");

        Long currentUserId = getCurrentUserIdAsLong();

        return postMapper.getList().stream()
                .map(post -> {
                    int likeCount = postLikeMapper.countByPostId(post.getPostId());
                    int commentCount = postMapper.countCommentsByPostId(post.getPostId());
                    int scrapCount = scrapMapper.countScrapsByPostId(post.getPostId()); // scrapCount 메서드 필요

                    post.setLikeCount(likeCount);
                    post.setCommentCount(commentCount);
                    post.setScrapCount(scrapCount);

                    boolean isLiked = false;
                    boolean isScraped = false;
                    if (currentUserId != null) {
                        isLiked = postLikeMapper.existsByPostIdAndMemberId(post.getPostId(), currentUserId);
                        isScraped = scrapMapper.existsScrap(post.getPostId(), currentUserId);
                    }

                    post.setLiked(isLiked);
                    post.setScraped(isScraped);

                    String nickname = memberMapper.getNicknameByMemberId(post.getMemberId());

                    return PostListResponseDTO.of(post, nickname);
                })
                .toList();
    }


    @Override
    public PostDetailsResponseDTO get(Long postId) {
        log.info("get..........");
        PostVO post = postMapper.get(postId);
        validatePostExists(postId);

        List<CommentVO> comments = postMapper.getCommentsByPostId(postId);
        Long currentUserId = getCurrentUserIdAsLong();
        int likeCount = postLikeMapper.countByPostId(post.getPostId());
        int commentCount = postMapper.countCommentsByPostId(post.getPostId());
        int scrapCount = scrapMapper.countScrapsByPostId(post.getPostId());

        post.setLikeCount(likeCount);
        post.setCommentCount(commentCount);
        post.setScrapCount(scrapCount);

        boolean isLiked = false;
        boolean isScraped = false;
        if (currentUserId != null) {
            isLiked = postLikeMapper.existsByPostIdAndMemberId(post.getPostId(), currentUserId);
            isScraped = scrapMapper.existsScrap(post.getPostId(), currentUserId);
        }
        post.setLiked(isLiked);
        post.setScraped(isScraped);
        String nickname = memberMapper.getNicknameByMemberId(post.getMemberId());

        return PostDetailsResponseDTO.of(post, comments, nickname);
    }

    @Override
    @Transactional
    public PostDetailsResponseDTO create(PostCreateRequestDTO postCreateRequestDTO) {
        log.info("create........." + postCreateRequestDTO);


        PostVO vo = postCreateRequestDTO.toVo();
        Long memberId = getCurrentUserIdAsLong();
        if (memberId == null) {
            throw new AccessDeniedException(ResponseCode.UNAUTHORIZED_USER);
        }
        vo.setMemberId(memberId);
        postMapper.create(vo);
        return get(vo.getPostId());
    }

    @Override
    public PostDetailsResponseDTO update(Long postId, PostUpdateRequestDTO postUpdateRequestDTO) {
        log.info("update........." + postUpdateRequestDTO);
        validatePostExists(postId);
        PostVO post = postMapper.get(postId);

        Long memberId = getCurrentUserIdAsLong();
        if (memberId == null) {
            throw new AccessDeniedException(ResponseCode.UNAUTHORIZED_USER);
        }
        if (post.getMemberId() != memberId) {
            throw new AccessDeniedException(ResponseCode.ACCESS_DENIED);
        }

        PostVO updateVO = postUpdateRequestDTO.toVo();
        updateVO.setPostId(postId);
        postMapper.update(updateVO);
        return get(postId);
    }


    @Override
    @Transactional
    public void delete(Long postId) {
        log.info("delete........." + postId);
        validatePostExists(postId);
        PostVO post = postMapper.get(postId);

        Long memberId = getCurrentUserIdAsLong();
        if (memberId == null) {
            throw new AccessDeniedException(ResponseCode.UNAUTHORIZED_USER);
        }
        if (post.getMemberId() != memberId) {
            throw new AccessDeniedException(ResponseCode.ACCESS_DENIED);
        }

        postMapper.delete(postId);
    }

    @Override
    public List<PostListResponseDTO> getListByBoard(Long boardId) {
        log.info("getListByBoard......... boardId={}, memberId={}", boardId);
        boolean isLiked = false;
        boolean isScraped = false;

        List<PostVO> posts = postMapper.getListByBoard(boardId);
        for (PostVO post : posts) {
            Long postId = post.getPostId();

            Long currentUserId = getCurrentUserIdAsLong();
            int likeCount = postLikeMapper.countByPostId(post.getPostId());
            int commentCount = postMapper.countCommentsByPostId(post.getPostId());
            int scrapCount = scrapMapper.countScrapsByPostId(post.getPostId());

            post.setLikeCount(likeCount);
            post.setCommentCount(commentCount);
            post.setScrapCount(scrapCount);

            if (currentUserId != null) {
                isLiked = postLikeMapper.existsByPostIdAndMemberId(post.getPostId(), currentUserId);
                isScraped = scrapMapper.existsScrap(post.getPostId(), currentUserId);
            }
            post.setLiked(isLiked);
            post.setScraped(isScraped);
        }

        return posts.stream()
                .map(post -> {
                    String nickname = memberMapper.getNicknameByMemberId(post.getMemberId());
                    return PostListResponseDTO.of(post, nickname);
                })
                .toList();
    }


    @Override
    public List<PostListResponseDTO> getMyPosts() {
        log.info("getMyPosts..........");
        Long memberId = getCurrentUserIdAsLong();
        if (memberId == null) {
            throw new AccessDeniedException(ResponseCode.UNAUTHORIZED_USER);
        }
        boolean isLiked = false;
        boolean isScraped = false;
        List<PostVO> posts = postMapper.getPostsByMemberId(memberId);

        for (PostVO post : posts) {
            Long postId = post.getPostId();

            int likeCount = postLikeMapper.countByPostId(post.getPostId());
            int commentCount = postMapper.countCommentsByPostId(post.getPostId());
            int scrapCount = scrapMapper.countScrapsByPostId(post.getPostId());

            post.setLikeCount(likeCount);
            post.setCommentCount(commentCount);
            post.setScrapCount(scrapCount);

            isLiked = postLikeMapper.existsByPostIdAndMemberId(post.getPostId(), memberId);
            isScraped = scrapMapper.existsScrap(post.getPostId(), memberId);
            post.setLiked(isLiked);
            post.setScraped(isScraped);
        }

        return posts.stream()
                .map(post -> {
                    String nickname = memberMapper.getNicknameByMemberId(post.getMemberId());
                    return PostListResponseDTO.of(post, nickname);
                })
                .toList();
    }

    @Override
    public List<PostListResponseDTO> getHotPostsByBoard(Long boardId) {
        log.info("getHotPostsByBoard......... boardId={}", boardId);

        String key = HOT_POSTS_BOARD_KEY_PREFIX + boardId;

        // Redis에서 먼저 조회
        try {
            @SuppressWarnings("unchecked")
            List<PostListResponseDTO> cachedPosts = (List<PostListResponseDTO>) redisObjectTemplate.opsForValue().get(key);
            if (cachedPosts != null && !cachedPosts.isEmpty()) {
                log.info("Redis에서 게시판 {} 핫게시물 조회 성공: {} 개", boardId, cachedPosts.size());
                return cachedPosts;
            }
        } catch (Exception e) {
            log.warn("Redis에서 핫게시물 조회 실패, DB에서 조회합니다: {}", e.getMessage());
        }

        // Redis에 없으면 DB에서 조회
        log.info("Redis에 데이터 없음, DB에서 조회");
        Long memberId = getCurrentUserIdAsLong();
        boolean isLiked = false;
        boolean isScraped = false;

        List<PostVO> posts = postMapper.getHotPostsByBoard(boardId);
        for (PostVO post : posts) {
            Long postId = post.getPostId();
            Long currentUserId = getCurrentUserIdAsLong();
            int likeCount = postLikeMapper.countByPostId(postId);
            int commentCount = postMapper.countCommentsByPostId(postId);
            int scrapCount = scrapMapper.countScrapsByPostId(postId);

            post.setLikeCount(likeCount);
            post.setCommentCount(commentCount);
            post.setScrapCount(scrapCount);

            if (currentUserId != null) {
                isLiked = postLikeMapper.existsByPostIdAndMemberId(post.getPostId(), currentUserId);
                isScraped = scrapMapper.existsScrap(post.getPostId(), currentUserId);
            }
            post.setLiked(isLiked);
            post.setScraped(isScraped);
        }

        List<PostListResponseDTO> result = posts.stream()
                .map(post -> {
                    String nickname = memberMapper.getNicknameByMemberId(post.getMemberId());
                    return PostListResponseDTO.of(post, nickname);
                })
                .toList();

        // Redis에 저장 (예외 처리 포함)
        try {
            redisObjectTemplate.opsForValue().set(key, result, Duration.ofDays(1));
            log.info("게시판 {} 핫게시물 {} 개 Redis에 저장 완료", boardId, result.size());
        } catch (Exception e) {
            log.warn("Redis 저장 실패: {}", e.getMessage());
        }

        return result;
    }

    @Override
    public List<PostListResponseDTO> getAllHotPosts() {
        log.info("getAllHotPosts..........");

        String key = HOT_POSTS_ALL_KEY;

        // Redis에서 먼저 조회
        try {
            @SuppressWarnings("unchecked")
            List<PostListResponseDTO> cachedPosts = (List<PostListResponseDTO>) redisObjectTemplate.opsForValue().get(key);
            if (cachedPosts != null && !cachedPosts.isEmpty()) {
                log.info("Redis에서 전체 핫게시물 조회 성공: {} 개", cachedPosts.size());
                return cachedPosts;
            }
        } catch (Exception e) {
            log.warn("Redis에서 핫게시물 조회 실패, DB에서 조회합니다: {}", e.getMessage());
        }
        Long memberId = getCurrentUserIdAsLong();
        boolean isLiked = false;
        boolean isScraped = false;
        // Redis에 없으면 DB에서 조회
        log.info("Redis에 데이터 없음, DB에서 조회");
        List<PostVO> posts = postMapper.getAllHotPosts();
        for (PostVO post : posts) {
            Long postId = post.getPostId();
            Long currentUserId = getCurrentUserIdAsLong();
            int likeCount = postLikeMapper.countByPostId(postId);
            int commentCount = postMapper.countCommentsByPostId(postId);
            int scrapCount = scrapMapper.countScrapsByPostId(postId);

            post.setLikeCount(likeCount);
            post.setCommentCount(commentCount);
            post.setScrapCount(scrapCount);

            if (currentUserId != null) {
                isLiked = postLikeMapper.existsByPostIdAndMemberId(post.getPostId(), currentUserId);
                isScraped = scrapMapper.existsScrap(post.getPostId(), currentUserId);
            }
            post.setLiked(isLiked);
            post.setScraped(isScraped);
        }

        List<PostListResponseDTO> result = posts.stream()
                .map(post -> {
                    String nickname = memberMapper.getNicknameByMemberId(post.getMemberId());
                    return PostListResponseDTO.of(post, nickname);
                })
                .toList();

        try {
            redisObjectTemplate.opsForValue().set(key, result, Duration.ofDays(1));
            log.info("전체 핫게시물 {} 개 Redis에 저장 완료", result.size());
        } catch (Exception e) {
            log.warn("Redis 저장 실패: {}", e.getMessage());
        }

        return result;
    }
    @Override
    public PageResponseDTO<PostListResponseDTO> getListWithPaging(PageRequestDTO pageRequest) {
        log.info("getListWithPaging..........");

        Long currentUserId = getCurrentUserIdAsLong();

        // 페이징된 게시글 목록 조회
        List<PostVO> posts = postMapper.getListWithPaging(pageRequest.getOffset(), pageRequest.getSize());

        // 전체 게시글 수 조회
        long totalCount = postMapper.getTotalCount();

        // PostVO를 PostListResponseDTO로 변환
        List<PostListResponseDTO> result = posts.stream()
                .map(post -> {
                    setPostCounts(post);
                    setUserInteractionFlags(post, currentUserId);
                    String nickname = memberMapper.getNicknameByMemberId(post.getMemberId());
                    return PostListResponseDTO.of(post, nickname);
                })
                .toList();

        return PageResponseDTO.of(result, pageRequest, totalCount);
    }

    @Override
    public PageResponseDTO<PostListResponseDTO> getListByBoardWithPaging(Long boardId, PageRequestDTO pageRequest) {
        log.info("getListByBoardWithPaging......... boardId={}", boardId);

        Long currentUserId = getCurrentUserIdAsLong();

        // 페이징된 게시글 목록 조회
        List<PostVO> posts = postMapper.getListByBoardWithPaging(boardId, pageRequest.getOffset(), pageRequest.getSize());

        // 해당 게시판의 전체 게시글 수 조회
        long totalCount = postMapper.getTotalCountByBoard(boardId);

        // PostVO를 PostListResponseDTO로 변환
        List<PostListResponseDTO> result = posts.stream()
                .map(post -> {
                    setPostCounts(post);
                    setUserInteractionFlags(post, currentUserId);
                    String nickname = memberMapper.getNicknameByMemberId(post.getMemberId());
                    return PostListResponseDTO.of(post, nickname);
                })
                .toList();

        return PageResponseDTO.of(result, pageRequest, totalCount);
    }

    @Override
    public PageResponseDTO<PostListResponseDTO> getMyPostsWithPaging(PageRequestDTO pageRequest) {
        log.info("getMyPostsWithPaging..........");

        Long memberId = getCurrentUserIdAsLong();
        if (memberId == null) {
            throw new AccessDeniedException(ResponseCode.UNAUTHORIZED_USER);
        }

        // 페이징된 게시글 목록 조회
        List<PostVO> posts = postMapper.getPostsByMemberIdWithPaging(memberId, pageRequest.getOffset(), pageRequest.getSize());

        // 해당 사용자의 전체 게시글 수 조회
        long totalCount = postMapper.getTotalCountByMemberId(memberId);

        // PostVO를 PostListResponseDTO로 변환
        List<PostListResponseDTO> result = posts.stream()
                .map(post -> {
                    setPostCounts(post);
                    setUserInteractionFlags(post, memberId);
                    String nickname = memberMapper.getNicknameByMemberId(post.getMemberId());
                    return PostListResponseDTO.of(post, nickname);
                })
                .toList();

        return PageResponseDTO.of(result, pageRequest, totalCount);
    }

    // 공통 메서드: 게시글의 좋아요, 댓글, 스크랩 수 설정
    private void setPostCounts(PostVO post) {
        int likeCount = postLikeMapper.countByPostId(post.getPostId());
        int commentCount = postMapper.countCommentsByPostId(post.getPostId());
        int scrapCount = scrapMapper.countScrapsByPostId(post.getPostId());

        post.setLikeCount(likeCount);
        post.setCommentCount(commentCount);
        post.setScrapCount(scrapCount);
    }

    // 공통 메서드: 사용자의 좋아요, 스크랩 여부 설정
    private void setUserInteractionFlags(PostVO post, Long currentUserId) {
        boolean isLiked = false;
        boolean isScraped = false;

        if (currentUserId != null) {
            isLiked = postLikeMapper.existsByPostIdAndMemberId(post.getPostId(), currentUserId);
            isScraped = scrapMapper.existsScrap(post.getPostId(), currentUserId);
        }

        post.setLiked(isLiked);
        post.setScraped(isScraped);
    }
    private Long getCurrentUserIdAsLong() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        String email = authentication.getName();
        return memberMapper.getMemberIdByEmail(email);
    }
    private void validatePostExists(Long postId) {
        if (!postMapper.existsById(postId)) {
            throw new PostNotFoundException(ResponseCode.POST_NOT_FOUND);
        }
    }
}
