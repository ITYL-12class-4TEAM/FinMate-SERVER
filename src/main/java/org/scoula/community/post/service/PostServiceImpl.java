package org.scoula.community.post.service;

import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.auth.exception.AccessDeniedException;
import org.scoula.community.comment.domain.CommentVO;
import org.scoula.community.post.domain.PostVO;
import org.scoula.community.post.domain.ProductTag;
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
    private final static String BASE_DIR = "/Users/yerong/documents/board";
    final private PostMapper postMapper;
    private final MemberMapper memberMapper;
    private final PostLikeMapper postLikeMapper;
    private final RedisTemplate<String, Object> redisObjectTemplate;
    private final PostLikeService postLikeService;
    private final ScrapService scrapService;
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

//        List<PostAttachmentVO> attachments = postMapper.getAttachmentList(postId);
//        post.setAttachments(attachments);

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
//        List<MultipartFile> files = postCreateRequestDTO.getFiles();
//        if (files != null && !files.isEmpty()) {
//            upload(vo.getPostId(), files);
//        }
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
//        List<MultipartFile> files = postUpdateRequestDTO.getFiles();
//        if (files != null && !files.isEmpty()) {
//            upload(post.getPostId(), files);
//        }
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
//        List<PostAttachmentVO> attachments = postMapper.getAttachmentList(postId);
//        for (PostAttachmentVO attachment : attachments) {
//            try {
//                UploadFiles.deleteFile(attachment.getPath());
//                log.info("파일 삭제 성공: {}", attachment.getPath());
//            } catch (Exception e) {
//                log.warn("파일 삭제 실패: {}", attachment.getPath(), e);
//            }
//        }
//        postMapper.deleteAttachmentsByPostId(postId);
        postMapper.delete(postId);
    }

//    @Override
//    public PostAttachmentVO getAttachment(Long no) {
//        PostAttachmentVO attachment = postMapper.getAttachment(no);
//        if (attachment == null) {
//            throw new AttachmentNotFound(ResponseCode.ATTACHMENT_NOT_FOUND);
//        }
//        return attachment;
//    }

//    // 첨부파일 삭제
//    @Override
//    @Transactional
//    public boolean deleteAttachment(Long no) {
//        // 첨부파일 정보 조회
//        PostAttachmentVO attachment = postMapper.getAttachment(no);
//        if (attachment == null) {
//            throw new AttachmentNotFound(ResponseCode.ATTACHMENT_NOT_FOUND);
//        }
//
//        PostVO post = postMapper.get(attachment.getBno());
//        Long memberId = getCurrentUserIdAsLong();
//        if (!post.getMemberId().equals(memberId)) {
//            throw new AccessDeniedException(ResponseCode.ACCESS_DENIED);
//        }
//
//        try {
//            UploadFiles.deleteFile(attachment.getPath());
//            log.info("첨부파일 삭제 성공: {}", attachment.getPath());
//        } catch (Exception e) {
//            log.warn("첨부파일 삭제 실패: {}", attachment.getPath(), e);
//        }
//
//        // DB 레코드 삭제
//        boolean result = postMapper.deleteAttachment(no) == 1;
//        if (!result) {
//            throw new AttachmentNotFound(ResponseCode.ATTACHMENT_NOT_FOUND);
//        }
//
//        return true;
//    }

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

//
//    private void upload(Long bno, List<MultipartFile> files) {
//        for (MultipartFile part : files) {
//            if (part == null || part.isEmpty()) continue;
//
//            try {
//                String originalFileName = UploadFiles.sanitizeFilename(part.getOriginalFilename());
//
//                if (part.getSize() > 50 * 1024 * 1024) {
//                    log.warn("파일 크기 초과: {} ({}bytes)", originalFileName, part.getSize());
//                    continue;
//                }
//
//                String uploadPath = UploadFiles.upload(BASE_DIR, part);
//                PostAttachmentVO attach = PostAttachmentVO.of(part, bno, uploadPath);
//                postMapper.createAttachment(attach);
//
//                log.info("파일 업로드 성공: {} -> {}", originalFileName, uploadPath);
//
//            } catch (IOException e) {
//                log.error("파일 업로드 실패: {}", part.getOriginalFilename(), e);
//                throw new UploadFailException(ResponseCode.FILE_UPLOAD_FAIL);
//            }
//        }
//    }

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
