package org.scoula.community.postlike.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.scoula.auth.exception.AccessDeniedException;
import org.scoula.community.comment.exception.CommentNotFoundException;
import org.scoula.community.post.domain.PostVO;
import org.scoula.community.post.dto.PostListResponseDTO;
import org.scoula.community.post.exception.PostNotFoundException;
import org.scoula.community.post.mapper.PostMapper;
import org.scoula.community.postlike.domain.PostLikeVO;
import org.scoula.community.postlike.mapper.PostLikeMapper;
import org.scoula.community.scrap.mapper.ScrapMapper;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.notification.helper.NotificationHelper;
import org.scoula.response.ResponseCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {
    private final PostLikeMapper postLikeMapper;
    private final PostMapper postMapper;
    private final MemberMapper memberMapper;
    private final ScrapMapper scrapMapper;
    private final NotificationHelper notificationHelper;


    @Override
    @Transactional
    public boolean toggleLike(Long postId) {
        validatePostExists(postId);
        Long memberId = getCurrentUserIdAsLong();
        if (memberId == null) {
            throw new AccessDeniedException(ResponseCode.UNAUTHORIZED_USER);
        }

        PostLikeVO like = postLikeMapper.findByPostIdAndMemberId(postId, memberId);

        if (like == null) {
            postLikeMapper.insert(PostLikeVO.builder()
                    .postId(postId)
                    .memberId(memberId)
                    .isLiked(true)
                    .build());
            // 알림 생성
            PostVO post = postMapper.get(postId);
            if (!post.getMemberId().equals(memberId)) {

                String authorNickname = memberMapper.getNicknameByMemberId(memberId);
                notificationHelper.notifyLikeCreated(
                        post.getPostId(),
                        memberId,
                        authorNickname,
                        post.getTitle()
                );
            }
            return true;
        } else {
            postLikeMapper.deleteByPostIdAndMemberId(postId, memberId);
            return false;
        }
    }

    @Override
    public boolean isLikedByMember(Long postId) {
        validatePostExists(postId);
        Long memberId = getCurrentUserIdAsLong();
        PostLikeVO like = postLikeMapper.findByPostIdAndMemberId(postId, memberId);
        return like != null && like.isLiked();
    }

    @Override
    public List<PostListResponseDTO> getMyLikedPosts() {
        Long memberId = getCurrentUserIdAsLong();

        return postLikeMapper.getLikedPostsByMemberId(memberId).stream()
                .map(post -> {
                    Long currentUserId = getCurrentUserIdAsLong();
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
                .toList();
    }


    @Override
    public int getLikeCount(Long postId) {
        validatePostExists(postId);

        return postLikeMapper.countByPostId(postId);
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
