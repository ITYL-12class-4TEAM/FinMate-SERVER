package org.scoula.community.comment.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.auth.exception.AccessDeniedException;
import org.scoula.community.comment.domain.CommentVO;
import org.scoula.community.comment.dto.CommentCreateRequestDTO;
import org.scoula.community.comment.dto.CommentResponseDTO;
import org.scoula.community.comment.exception.CommentNotFoundException;
import org.scoula.community.comment.exception.CommentParentMismatchException;
import org.scoula.community.comment.mapper.CommentMapper;
import org.scoula.community.commentlike.mapper.CommentLikeMapper;
import org.scoula.community.commentlike.service.CommentLikeService;
import org.scoula.community.post.exception.PostNotFoundException;
import org.scoula.community.post.mapper.PostMapper;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.response.ResponseCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    private final MemberMapper memberMapper;
    private final CommentLikeService commentLikeService;
    private final CommentLikeMapper commentLikeMapper;

    @Override
    @Transactional
    public CommentResponseDTO create(CommentCreateRequestDTO commentCreateRequestDTO) {
        log.info("create........." + commentCreateRequestDTO);
        if (!postMapper.existsById(commentCreateRequestDTO.getPostId())) {
            throw new PostNotFoundException(ResponseCode.POST_NOT_FOUND);
        }
        if (commentCreateRequestDTO.getParentComment() != null) {
            CommentVO parent = commentMapper.get(commentCreateRequestDTO.getParentComment());
            if (parent == null) {
                throw new CommentParentMismatchException(ResponseCode.COMMENT_PARENT_MISMATCH);
            }
            if (!parent.getPostId().equals(commentCreateRequestDTO.getPostId())) {
                throw new CommentParentMismatchException(ResponseCode.COMMENT_PARENT_MISMATCH);
            }
        }

        CommentVO vo = commentCreateRequestDTO.toVo();
        vo.setMemberId(getCurrentUserIdAsLong());
        commentMapper.create(vo);
        postMapper.incrementCommentCount(vo.getPostId());

        return get(vo.getCommentId());
    }

    @Override
    public CommentResponseDTO get(Long commentId) {
        log.info("get..........");

        CommentVO comment = commentMapper.get(commentId);
        if (comment == null) {
            throw new CommentNotFoundException(ResponseCode.COMMENT_NOT_FOUND);
        }
        Long currentUserId = getCurrentUserIdAsLong();
        boolean isLiked = false;

        if (currentUserId != null) {
            isLiked = commentLikeService.isLikedByMember(commentId, currentUserId);
        }
        return CommentResponseDTO.of(comment, isLiked, memberMapper.getNicknameByMemberId(currentUserId));
    }

    @Transactional
    public void delete(Long commentId) {
        log.info("delete........." + commentId);
        CommentVO comment = commentMapper.get(commentId);
        if (comment == null) {
            throw new CommentNotFoundException(ResponseCode.COMMENT_NOT_FOUND);
        }
        Long memberId = getCurrentUserIdAsLong();
        if (comment.getMemberId()!= memberId) {
            throw new AccessDeniedException(ResponseCode.ACCESS_DENIED);
        }
        int deleteCount;
        if (comment.getParentComment() == null) {
            deleteCount = commentMapper.countAllByParentOrSelf(commentId);
            commentMapper.deleteParentAndChildren(commentId);
        } else {
            // 자식 댓글: 본인만 삭제
            deleteCount = 1;
            commentMapper.deleteChild(commentId);
        }

        postMapper.decrementCommentCountBy(comment.getPostId(), deleteCount);
    }

    @Override
    public List<CommentResponseDTO> getListByPostId(Long postId) {
        log.info("getListByPostId..........");
        if (!postMapper.existsById(postId)) {
            throw new PostNotFoundException(ResponseCode.POST_NOT_FOUND);
        }
        List<CommentVO> comments = commentMapper.getListByPostId(postId);
        if (comments == null || comments.isEmpty()) {
            return List.of();
        }

        Long currentUserId = getCurrentUserIdAsLong();
        if (currentUserId == null) {
            // 로그인 안 된 사용자면 isLiked false로 처리
            return comments.stream()
                    .map(comment -> CommentResponseDTO.of(comment, false, memberMapper.getNicknameByMemberId(currentUserId)))
                    .toList();
        }

        List<Long> commentIds = comments.stream()
                .map(CommentVO::getCommentId)
                .toList();

        List<Long> likedCommentIds = commentLikeMapper.findLikedCommentIdsByMemberIdAndCommentIds(currentUserId, commentIds);

        return comments.stream()
                .map(comment -> {
                    boolean isLiked = likedCommentIds.contains(comment.getCommentId());
                    return CommentResponseDTO.of(comment, isLiked, memberMapper.getNicknameByMemberId(currentUserId));
                })
                .toList();
    }

    @Override
    public List<CommentResponseDTO> getParentAndReplies(Long parentCommentId) {
        log.info("getParentAndReplies..........");
        CommentVO parent = commentMapper.get(parentCommentId);
        if (parent == null) {
            throw new CommentNotFoundException(ResponseCode.COMMENT_NOT_FOUND);
        }

        List<CommentVO> comments = commentMapper.getParentAndReplies(parentCommentId);
        if (comments == null || comments.isEmpty()) {
            return List.of();
        }

        Long currentUserId = getCurrentUserIdAsLong();
        if (currentUserId == null) {
            return comments.stream()
                    .map(comment -> CommentResponseDTO.of(comment, false, memberMapper.getNicknameByMemberId(currentUserId)))
                    .toList();
        }

        List<Long> commentIds = comments.stream()
                .map(CommentVO::getCommentId)
                .toList();

        List<Long> likedCommentIds = commentLikeMapper.findLikedCommentIdsByMemberIdAndCommentIds(currentUserId, commentIds);

        return comments.stream()
                .map(comment -> {
                    boolean isLiked = likedCommentIds.contains(comment.getCommentId());
                    return CommentResponseDTO.of(comment, isLiked, memberMapper.getNicknameByMemberId(currentUserId));
                })
                .toList();
    }

    @Override
    public List<CommentResponseDTO> getMyComments() {
        log.info("getMyComments..........");
        Long currentUserId = getCurrentUserIdAsLong();

        List<CommentVO> comments = commentMapper.getCommentsByMemberId(currentUserId);
        if (comments == null || comments.isEmpty()) {
            return List.of();
        }

        List<Long> commentIds = comments.stream()
                .map(CommentVO::getCommentId)
                .toList();

        List<Long> likedCommentIds = commentLikeMapper.findLikedCommentIdsByMemberIdAndCommentIds(currentUserId, commentIds);

        return comments.stream()
                .map(comment -> {
                    boolean isLiked = likedCommentIds.contains(comment.getCommentId());
                    return CommentResponseDTO.of(comment, isLiked, memberMapper.getNicknameByMemberId(currentUserId));
                })
                .toList();
    }


    private Long getCurrentUserIdAsLong() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberMapper.getMemberIdByEmail(email); // 👈 이메일로 memberId 조회하는 쿼리 필요
    }
}
