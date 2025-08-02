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
import org.scoula.member.mapper.MemberMapper;
import org.scoula.response.ResponseCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentLikeServiceImpl implements CommentLikeService {
    private final CommentLikeMapper commentLikeMapper;
    private final CommentMapper commentMapper;
    private final MemberMapper memberMapper;

    @Override
    @Transactional
    public boolean toggleLike(Long commentId) {
        Long memberId = getCurrentUserIdAsLong();
        if (memberId == null) {
            throw new AccessDeniedException(ResponseCode.UNAUTHORIZED_USER);
        }
        if (!commentMapper.existsById(commentId)) {
            throw new CommentNotFoundException(ResponseCode.COMMENT_NOT_FOUND);
        }

        CommentLikeVO like = commentLikeMapper.findByCommentIdAndMemberId(commentId, memberId);

        if (like == null) {
            commentLikeMapper.insert(CommentLikeVO.builder()
                    .commentId(commentId)
                    .memberId(memberId)
                    .isLiked(true)
                    .build());
            return true;
        } else {
            commentLikeMapper.deleteByCommentIdAndMemberId(commentId, memberId);
            return false;
        }
    }

    @Override
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
    private Long getCurrentUserIdAsLong() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        String email = authentication.getName();
        return memberMapper.getMemberIdByEmail(email);
    }
}
