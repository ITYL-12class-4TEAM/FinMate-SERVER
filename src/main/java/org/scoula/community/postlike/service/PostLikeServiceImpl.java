package org.scoula.community.postlike.service;

import lombok.RequiredArgsConstructor;
import org.scoula.auth.exception.AccessDeniedException;
import org.scoula.community.comment.exception.CommentNotFoundException;
import org.scoula.community.post.domain.PostVO;
import org.scoula.community.post.exception.PostNotFoundException;
import org.scoula.community.post.mapper.PostMapper;
import org.scoula.community.postlike.domain.PostLikeVO;
import org.scoula.community.postlike.mapper.PostLikeMapper;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.response.ResponseCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {
    private final PostLikeMapper postLikeMapper;
    private final PostMapper postMapper;
    private final MemberMapper memberMapper;

    @Override
    @Transactional
    public boolean toggleLike(Long postId) {
        Long memberId = getCurrentUserIdAsLong();

        PostLikeVO like = postLikeMapper.findByPostIdAndMemberId(postId, memberId);

        if (like == null) {
            postLikeMapper.insert(PostLikeVO.builder()
                    .postId(postId)
                    .memberId(memberId)
                    .isLiked(true)
                    .build());
            return true;
        } else {
            postLikeMapper.deleteByPostIdAndMemberId(postId, memberId);
            return false;
        }
    }

    @Override
    public boolean isLikedByMember(Long postId, Long memberId) {
        if (!postMapper.existsById(postId)) {
            throw new PostNotFoundException(ResponseCode.POST_NOT_FOUND);
        }
        PostLikeVO like = postLikeMapper.findByPostIdAndMemberId(postId, memberId);
        return like != null && like.isLiked();
    }

    @Override
    public int getLikeCount(Long postId) {
        if (!postMapper.existsById(postId)) {
            throw new PostNotFoundException(ResponseCode.POST_NOT_FOUND);
        }

        return postLikeMapper.countByPostId(postId);
    }
    private Long getCurrentUserIdAsLong() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberMapper.getMemberIdByEmail(email); // 👈 이메일로 memberId 조회하는 쿼리 필요
    }
}
