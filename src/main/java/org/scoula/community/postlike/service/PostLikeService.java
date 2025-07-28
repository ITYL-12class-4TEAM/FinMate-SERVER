package org.scoula.community.postlike.service;

public interface PostLikeService {
    boolean toggleLike(Long postId);
    int getLikeCount(Long postId);
    boolean isLikedByMember(Long postId, Long memberId);
}
