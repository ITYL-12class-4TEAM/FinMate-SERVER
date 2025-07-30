package org.scoula.community.post.mapper;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.community.comment.domain.CommentVO;
import org.scoula.community.post.domain.PostAttachmentVO;
import org.scoula.community.post.domain.PostVO;
import org.scoula.community.post.dto.PostListResponseDTO;


@Mapper
public interface PostMapper {
    List<PostVO> getListByBoard(Long boardId);
    public List<PostVO> getList();
    public PostVO get(Long no);
    public void create(PostVO board);
    public int update(PostVO board);
    public int delete(Long no);
    boolean existsById(Long postId);

//    public void createAttachment(PostAttachmentVO attach);
//    public List<PostAttachmentVO> getAttachmentList(Long bno);
//    public PostAttachmentVO getAttachment(Long no);
//    public int deleteAttachment(Long no);

    List<CommentVO> getCommentsByPostId(Long no);
    void updateLikeCount(Long postId);
    int countCommentsByPostId(Long postId);

    void incrementCommentCount(Long postId);
    void decrementCommentCountBy(@Param("postId") Long postId, @Param("count") int count);

//    void deleteAttachmentsByPostId(Long postId);

    List<PostVO> getPostsByMemberId(Long currentUserId);

    PostVO findOldestPostByBoardId(Long hotBoardId);
    int countPostsByBoardId(Long hotBoardId);

    List<PostVO> getHotPostsByBoard(Long boardId);
    List<PostVO> getAllHotPosts();
}
