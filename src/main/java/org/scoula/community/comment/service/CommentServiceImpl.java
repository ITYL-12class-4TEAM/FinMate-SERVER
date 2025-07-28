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

    @Override
    @Transactional
    public CommentResponseDTO create(CommentCreateRequestDTO commentCreateRequestDTO) {
        log.info("create........." + commentCreateRequestDTO);
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
        return CommentResponseDTO.of(comment);
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
        List<CommentVO> comments = commentMapper.getListByPostId(postId);
        if (comments == null || comments.isEmpty()) {
            return List.of();
        }
        return comments.stream()
                .map(CommentResponseDTO::of)
                .toList();
    }

    @Override
    public List<CommentResponseDTO> getParentAndReplies(Long parentCommentId) {
        log.info("getParentAndReplies..........");
        // 부모 댓글과 그에 대한 대댓글을 조회
        List<CommentVO> comments = commentMapper.getParentAndReplies(parentCommentId);
        if (comments == null || comments.isEmpty()) {
            return List.of();
        }
        return comments.stream()
                .map(CommentResponseDTO::of)
                .toList();
    }

    private Long getCurrentUserIdAsLong() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberMapper.getMemberIdByEmail(email); // 👈 이메일로 memberId 조회하는 쿼리 필요
    }
}
