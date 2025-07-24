package org.scoula.community.postlike.service;

import lombok.RequiredArgsConstructor;
import org.scoula.community.postlike.domain.PostLikeVO;
import org.scoula.community.postlike.mapper.PostLikeMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {
    private final PostLikeMapper postLikeMapper;
    public boolean toggleLike(Long postId, Long memberId) {
        PostLikeVO existing = postLikeMapper.findByPostIdAndMemberId(postId, memberId);

        if (existing == null) {
            postLikeMapper.insert(PostLikeVO.builder()
                    .postId(postId)
                    .memberId(memberId)
                    .isLiked(true)
                    .build());
            return true;
        } else {
            boolean newStatus = !existing.isLiked();
            existing.setLiked(newStatus);
            postLikeMapper.update(existing);
            return newStatus;
        }
    }

    public int getLikeCount(Long postId) {
        return postLikeMapper.countByPostId(postId);
    }
}
