package org.scoula.community.commentlike.service;

public interface CommentLikeService {
    boolean toggleLike(Long commentId);
    int getLikeCount(Long commentId);
    boolean isLikedByMember(Long commentId, Long memberId);
}
