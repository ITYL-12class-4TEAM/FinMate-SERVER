package org.scoula.community.comment.service;

import java.util.List;
import org.scoula.community.comment.dto.CommentCreateRequestDTO;
import org.scoula.community.comment.dto.CommentResponseDTO;

public interface CommentService {
    CommentResponseDTO create(CommentCreateRequestDTO commentCreateRequestDTO);
    CommentResponseDTO get(Long postId);
    void delete(Long commentId);
    List<CommentResponseDTO> getListByPostId(Long postId);
    List<CommentResponseDTO> getParentAndReplies(Long parentCommentId);
    List<CommentResponseDTO> getMyComments();
}
