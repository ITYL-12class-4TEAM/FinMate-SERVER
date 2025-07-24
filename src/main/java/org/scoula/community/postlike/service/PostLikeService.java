package org.scoula.community.postlike.service;

public interface PostLikeService {
    boolean toggleLike(Long postId, Long memberId);
    int getLikeCount(Long postId);
}
