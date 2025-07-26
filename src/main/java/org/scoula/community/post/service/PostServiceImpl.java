package org.scoula.community.post.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.auth.exception.AccessDeniedException;
import org.scoula.common.util.UploadFiles;
import org.scoula.community.comment.domain.CommentVO;
import org.scoula.community.post.domain.CategoryTag;
import org.scoula.community.post.domain.PostAttachmentVO;
import org.scoula.community.post.domain.PostVO;
import org.scoula.community.post.domain.ProductTag;
import org.scoula.community.post.dto.PostCreateRequestDTO;
import org.scoula.community.post.dto.PostDetailsResponseDTO;
import org.scoula.community.post.dto.PostListResponseDTO;
import org.scoula.community.post.dto.PostUpdateRequestDTO;
import org.scoula.community.post.exception.AttachmentNotFound;
import org.scoula.community.post.exception.InvalidTagException;
import org.scoula.community.post.exception.PostNotFoundException;
import org.scoula.community.post.exception.UploadFailException;
import org.scoula.community.post.mapper.PostMapper;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.response.ResponseCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Log4j2
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final static String BASE_DIR = "/Users/yerong/documents/board";
    final private PostMapper postMapper;
    private final MemberMapper memberMapper;

    @Override
    public List<PostListResponseDTO> getList() {
        log.info("getList..........");
        return postMapper.getList().stream()
                .map(PostListResponseDTO::of)
                .toList();
    }

    @Override
    public PostDetailsResponseDTO get(Long no) {
        log.info("get..........");
        PostVO post = postMapper.get(no);
        if (post == null) {
            throw new PostNotFoundException(ResponseCode.POST_NOT_FOUND);
        }

        List<CommentVO> comments = postMapper.getCommentsByPostId(no);
        int commentCount = postMapper.countCommentsByPostId(no);
        post.setCommentCount(commentCount);

        return PostDetailsResponseDTO.of(post, comments);
    }

    @Override
    @Transactional
    public PostDetailsResponseDTO create(PostCreateRequestDTO postCreateRequestDTO) {
        log.info("create........." + postCreateRequestDTO);
        validateTags(postCreateRequestDTO.getCategoryTag(), postCreateRequestDTO.getProductTag());

        PostVO vo = postCreateRequestDTO.toVo();
        vo.setMemberId(getCurrentUserIdAsLong());
        postMapper.create(vo);
        List<MultipartFile> files = postCreateRequestDTO.getFiles();
        if (files != null && !files.isEmpty()) {
            upload(vo.getPostId(), files);
        }
        return get(vo.getPostId());
    }

    @Override
    public PostDetailsResponseDTO update(Long postId, PostUpdateRequestDTO postUpdateRequestDTO) {
        log.info("update........." + postUpdateRequestDTO);
        validateTags(postUpdateRequestDTO.getCategoryTag(), postUpdateRequestDTO.getProductTag());

        PostVO post = postMapper.get(postId);
        if (post == null) {
            throw new PostNotFoundException(ResponseCode.POST_NOT_FOUND);
        }
        Long memberId = getCurrentUserIdAsLong();
        if (post.getMemberId() != memberId) {
            throw new AccessDeniedException(ResponseCode.ACCESS_DENIED);
        }
        postMapper.update(postUpdateRequestDTO.toVo());
        return get(postId);
    }


    @Override
    @Transactional
    public void delete(Long postId) {
        log.info("delete........." + postId);
        PostVO post = postMapper.get(postId);
        if (post == null) {
            throw new PostNotFoundException(ResponseCode.POST_NOT_FOUND);
        }
        Long memberId = getCurrentUserIdAsLong();
        if (post.getMemberId() != memberId) {
            throw new AccessDeniedException(ResponseCode.ACCESS_DENIED);
        }
        postMapper.delete(postId);
    }

    @Override
    public PostAttachmentVO getAttachment(Long no) {
        PostAttachmentVO attachment = postMapper.getAttachment(no);
        if (attachment == null) {
            throw new AttachmentNotFound(ResponseCode.ATTACHMENT_NOT_FOUND);
        }
        return attachment;
    }

    // 첨부파일 삭제
    @Override
    public boolean deleteAttachment(Long no) {
        boolean result = postMapper.deleteAttachment(no) == 1;
        if (!result) {
            throw new AttachmentNotFound(ResponseCode.ATTACHMENT_NOT_FOUND);
        }
        return true;
    }

    private void upload(Long bno, List<MultipartFile> files) {
        for (MultipartFile part : files) {
            if (part == null || part.isEmpty()) continue;

            try {
                String uploadPath = UploadFiles.upload(BASE_DIR, part);
                PostAttachmentVO attach = PostAttachmentVO.of(part, bno, uploadPath);
                postMapper.createAttachment(attach);
            } catch (IOException e) {
                throw new UploadFailException(ResponseCode.FILE_UPLOAD_FAIL);
            }
        }
    }
    private Long getCurrentUserIdAsLong() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberMapper.getMemberIdByEmail(email); // 👈 이메일로 memberId 조회하는 쿼리 필요
    }
    private void validateTags(String categoryTag, String productTag) {
        if (categoryTag != null && !CategoryTag.isValidCode(categoryTag)) {
            System.out.println("1!false");
            throw new InvalidTagException(ResponseCode.INVALID_CATEGORY_TAG);
        }

        if (productTag != null && !ProductTag.isValidCode(productTag)) {
            System.out.println("2!false");
            throw new InvalidTagException(ResponseCode.INVALID_PRODUCT_TAG);
        }
    }
}
