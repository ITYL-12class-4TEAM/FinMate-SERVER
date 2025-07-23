package org.scoula.community.comment.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.community.comment.domain.CommentVO;
import org.scoula.community.comment.dto.CommentCreateRequestDTO;
import org.scoula.community.comment.dto.CommentResponseDTO;
import org.scoula.community.comment.exception.CommentNotFoundException;
import org.scoula.community.comment.mapper.CommentMapper;
import org.scoula.response.ResponseCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    @Override
    @Transactional
    public CommentResponseDTO create(CommentCreateRequestDTO commentCreateRequestDTO) {
        log.info("create........." + commentCreateRequestDTO);

        CommentVO vo = commentCreateRequestDTO.toVo();
        commentMapper.create(vo);
        commentCreateRequestDTO.setCommentId(vo.getCommentId());
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

    @Override
    public List<CommentResponseDTO> getList() {
        log.info("getList..........");
        return commentMapper.getList().stream()
                .map(CommentResponseDTO::of)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long commentId) {
        log.info("delete........." + commentId);
        CommentResponseDTO commentResponseDTO = get(commentId);
        commentMapper.delete(commentId);
    }
}
