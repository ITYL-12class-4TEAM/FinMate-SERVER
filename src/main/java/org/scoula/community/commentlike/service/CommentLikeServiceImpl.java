package org.scoula.community.commentlike.service;

import lombok.RequiredArgsConstructor;
import org.scoula.auth.exception.AccessDeniedException;
import org.scoula.community.comment.domain.CommentVO;
import org.scoula.community.comment.exception.CommentNotFoundException;
import org.scoula.community.comment.mapper.CommentMapper;
import org.scoula.community.commentlike.domain.CommentLikeVO;
import org.scoula.community.commentlike.mapper.CommentLikeMapper;
import org.scoula.community.post.exception.PostNotFoundException;
import org.scoula.community.postlike.domain.PostLikeVO;
import org.scoula.response.ResponseCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentLikeServiceImpl implements CommentLikeService {
    private final CommentLikeMapper commentLikeMapper;
    private final CommentMapper commentMapper;

    public boolean toggleLike(Long commentId, Long memberId) {
        CommentVO comment = commentMapper.findById(commentId);
        if (comment == null) {
            throw new CommentNotFoundException(ResponseCode.COMMENT_NOT_FOUND);
        }
        String email = getCurrentUserId();
        if (!comment.getMemberId().toString().equals(email)) {
            throw new AccessDeniedException(ResponseCode.ACCESS_DENIED);
        }
        CommentLikeVO existing = commentLikeMapper.findByCommentIdAndMemberId(commentId, memberId);

        if (existing == null) {
            commentLikeMapper.insert(CommentLikeVO.builder()
                    .commentId(commentId)
                    .memberId(memberId)
                    .isLiked(true)
                    .build());
        } else {
            boolean newStatus = !existing.isLiked();
            existing.setLiked(newStatus);
            commentLikeMapper.update(existing);
        }

        commentMapper.updateLikeCount(commentId);

        CommentLikeVO finalLike = commentLikeMapper.findByCommentIdAndMemberId(commentId, memberId);
        return finalLike != null && finalLike.isLiked();
    }

    public int getLikeCount(Long commentId) {
        if (!commentMapper.existsById(commentId)) {
            throw new CommentNotFoundException(ResponseCode.COMMENT_NOT_FOUND);
        }

        return commentLikeMapper.countByCommentId(commentId);
    }

    @Override
    public boolean isLikedByMember(Long commentId, Long memberId) {
        if (!commentMapper.existsById(commentId)) {
            throw new CommentNotFoundException(ResponseCode.COMMENT_NOT_FOUND);
        }
        CommentLikeVO like = commentLikeMapper.findByCommentIdAndMemberId(commentId, memberId);
        return like != null && like.isLiked();
    }
    private String getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return email;
    }
}
