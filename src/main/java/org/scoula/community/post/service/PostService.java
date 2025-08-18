package org.scoula.community.post.service;

import java.util.List;
import org.scoula.community.post.domain.PostAttachmentVO;
import org.scoula.community.post.dto.PageRequestDTO;
import org.scoula.community.post.dto.PageResponseDTO;
import org.scoula.community.post.dto.PostCreateRequestDTO;
import org.scoula.community.post.dto.PostDetailsResponseDTO;
import org.scoula.community.post.dto.PostListResponseDTO;
import org.scoula.community.post.dto.PostUpdateRequestDTO;


public interface PostService {
    public List<PostListResponseDTO> getList();
    public PostDetailsResponseDTO get(Long postId);
    public PostDetailsResponseDTO create(PostCreateRequestDTO postCreateRequestDTO);
    public PostDetailsResponseDTO update(Long postId, PostUpdateRequestDTO postCreateRequestDTO);
    public void delete(Long no);
    public List<PostListResponseDTO> getListByBoard(Long boardId);
    List<PostListResponseDTO> getMyPosts();
    List<PostListResponseDTO> getHotPostsByBoard(Long boardId);
    List<PostListResponseDTO> getAllHotPosts();

    PageResponseDTO<PostListResponseDTO> getListWithPaging(PageRequestDTO pageRequest);
    PageResponseDTO<PostListResponseDTO> getListByBoardWithPaging(Long boardId, PageRequestDTO pageRequest);
    PageResponseDTO<PostListResponseDTO> getMyPostsWithPaging(PageRequestDTO pageRequest);
}
