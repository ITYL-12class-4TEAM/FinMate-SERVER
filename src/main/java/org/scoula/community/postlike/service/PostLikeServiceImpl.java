package org.scoula.community.postlike.service;

import lombok.RequiredArgsConstructor;
import org.scoula.community.comment.exception.CommentNotFoundException;
import org.scoula.community.post.exception.PostNotFoundException;
import org.scoula.community.post.mapper.PostMapper;
import org.scoula.community.postlike.domain.PostLikeVO;
import org.scoula.community.postlike.mapper.PostLikeMapper;
import org.scoula.response.ResponseCode;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {
    private final PostLikeMapper postLikeMapper;
    private final PostMapper postMapper;

    public boolean toggleLike(Long postId, Long memberId) {
        if (!postMapper.existsById(postId)) {
            throw new PostNotFoundException(ResponseCode.POST_NOT_FOUND);
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
}
