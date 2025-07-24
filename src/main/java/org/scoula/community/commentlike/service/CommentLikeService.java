package org.scoula.community.commentlike.service;

public interface CommentLikeService {
    boolean toggleLike(Long commentId, Long memberId);
    int getLikeCount(Long commentId);
}
