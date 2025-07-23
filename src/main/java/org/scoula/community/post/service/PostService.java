package org.scoula.community.post.service;

import java.util.List;
import org.scoula.community.post.domain.PostAttachmentVO;
import org.scoula.community.post.dto.PostCreateRequestDTO;
import org.scoula.community.post.dto.PostDetailsResponseDTO;
import org.scoula.community.post.dto.PostListResponseDTO;
import org.scoula.community.post.dto.PostUpdateRequestDTO;


public interface PostService {
    public List<PostListResponseDTO> getList();
    public PostDetailsResponseDTO get(Long no);
    public PostDetailsResponseDTO create(PostCreateRequestDTO postCreateRequestDTO);
    public PostDetailsResponseDTO update(PostUpdateRequestDTO postCreateRequestDTO);
    public void delete(Long no);
    public PostAttachmentVO getAttachment(Long no);
    public boolean deleteAttachment(Long no);

}
