package org.scoula.community.comment.service;

import java.util.List;
import org.scoula.community.comment.dto.CommentCreateRequestDTO;
import org.scoula.community.comment.dto.CommentResponseDTO;

public interface CommentService {
    CommentResponseDTO create(CommentCreateRequestDTO commentCreateRequestDTO);
    CommentResponseDTO get(Long postId);
    List<CommentResponseDTO> getList();
    void delete(Long commentId);

}
