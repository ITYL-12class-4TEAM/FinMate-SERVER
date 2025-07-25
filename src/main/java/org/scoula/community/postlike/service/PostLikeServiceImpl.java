package org.scoula.community.postlike.service;

import lombok.RequiredArgsConstructor;
import org.scoula.auth.exception.AccessDeniedException;
import org.scoula.community.comment.exception.CommentNotFoundException;
import org.scoula.community.post.domain.PostVO;
import org.scoula.community.post.exception.PostNotFoundException;
import org.scoula.community.post.mapper.PostMapper;
import org.scoula.community.postlike.domain.PostLikeVO;
import org.scoula.community.postlike.mapper.PostLikeMapper;
import org.scoula.response.ResponseCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {
    private final PostLikeMapper postLikeMapper;
    private final PostMapper postMapper;

    public boolean toggleLike(Long postId, Long memberId) {
        PostVO post = postMapper.get(postId);
        if (post == null) {
            throw new PostNotFoundException(ResponseCode.POST_NOT_FOUND);
        }
        String email = getCurrentUserId();
        if (!post.getMemberId().toString().equals(email)) {
            throw new AccessDeniedException(ResponseCode.ACCESS_DENIED);
        }
        PostLikeVO existing = postLikeMapper.findByPostIdAndMemberId(postId, memberId);

        if (existing == null) {
            postLikeMapper.insert(PostLikeVO.builder()
                    .postId(postId)
                    .memberId(memberId)
                    .isLiked(true)
                    .build());
        } else {
            boolean newStatus = !existing.isLiked();
            existing.setLiked(newStatus);
            postLikeMapper.update(existing);
        }

        postMapper.updateLikeCount(postId);

        PostLikeVO finalLike = postLikeMapper.findByPostIdAndMemberId(postId, memberId);
        return finalLike != null && finalLike.isLiked();
    }


    public int getLikeCount(Long postId) {
        if (!postMapper.existsById(postId)) {
            throw new PostNotFoundException(ResponseCode.POST_NOT_FOUND);
        }
        return postLikeMapper.countByPostId(postId);
    }


    @Override
    public boolean isLikedByMember(Long postId, Long memberId) {
        if (!postMapper.existsById(postId)) {
            throw new PostNotFoundException(ResponseCode.POST_NOT_FOUND);
        }
        PostLikeVO like = postLikeMapper.findByPostIdAndMemberId(postId, memberId);
        return like != null && like.isLiked();
    }
    private String getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return email;
    }
}
