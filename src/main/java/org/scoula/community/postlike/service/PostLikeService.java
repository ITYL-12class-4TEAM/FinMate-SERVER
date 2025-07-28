package org.scoula.community.postlike.service;

import java.util.List;
import org.scoula.community.post.domain.PostVO;
import org.scoula.community.post.dto.PostListResponseDTO;

public interface PostLikeService {
    boolean toggleLike(Long postId);
    int getLikeCount(Long postId);
    boolean isLikedByMember(Long postId, Long memberId);
    List<PostListResponseDTO> getMyLikedPosts();
}
