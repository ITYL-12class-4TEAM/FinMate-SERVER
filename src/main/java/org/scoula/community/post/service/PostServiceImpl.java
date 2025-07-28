package org.scoula.community.post.service;

import java.io.IOException;
import java.util.List;
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
        List<PostAttachmentVO> attachments = postMapper.getAttachmentList(no);
        post.setAttachments(attachments);

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
        List<MultipartFile> files = postUpdateRequestDTO.getFiles();
        if (files != null && !files.isEmpty()) {
            upload(post.getPostId(), files);
        }
        PostVO updateVO = postUpdateRequestDTO.toVo();
        updateVO.setPostId(postId);
        postMapper.update(updateVO);
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
        List<PostAttachmentVO> attachments = postMapper.getAttachmentList(postId);
        for (PostAttachmentVO attachment : attachments) {
            try {
                UploadFiles.deleteFile(attachment.getPath());
                log.info("파일 삭제 성공: {}", attachment.getPath());
            } catch (Exception e) {
                log.warn("파일 삭제 실패: {}", attachment.getPath(), e);
            }
        }
        postMapper.deleteAttachmentsByPostId(postId);
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
    @Transactional
    public boolean deleteAttachment(Long no) {
        // 첨부파일 정보 조회
        PostAttachmentVO attachment = postMapper.getAttachment(no);
        if (attachment == null) {
            throw new AttachmentNotFound(ResponseCode.ATTACHMENT_NOT_FOUND);
        }

        PostVO post = postMapper.get(attachment.getBno());
        Long memberId = getCurrentUserIdAsLong();
        if (!post.getMemberId().equals(memberId)) {
            throw new AccessDeniedException(ResponseCode.ACCESS_DENIED);
        }

        try {
            UploadFiles.deleteFile(attachment.getPath());
            log.info("첨부파일 삭제 성공: {}", attachment.getPath());
        } catch (Exception e) {
            log.warn("첨부파일 삭제 실패: {}", attachment.getPath(), e);
        }

        // DB 레코드 삭제
        boolean result = postMapper.deleteAttachment(no) == 1;
        if (!result) {
            throw new AttachmentNotFound(ResponseCode.ATTACHMENT_NOT_FOUND);
        }

        return true;
    }

    @Override
    public List<PostListResponseDTO> getListByBoard(Long boardId) {
        log.info("getListByBoard......... boardId={}", boardId);
        return postMapper.getListByBoard(boardId).stream()
                .map(PostListResponseDTO::of)
                .toList();
    }

    @Override
    public List<PostListResponseDTO> getMyPosts() {
        log.info("getMyPosts..........");
        Long currentUserId = getCurrentUserIdAsLong();
        return postMapper.getPostsByMemberId(currentUserId).stream()
                .map(PostListResponseDTO::of)
                .toList();
    }

    private void upload(Long bno, List<MultipartFile> files) {
        for (MultipartFile part : files) {
            if (part == null || part.isEmpty()) continue;

            try {
                String originalFileName = UploadFiles.sanitizeFilename(part.getOriginalFilename());

                if (part.getSize() > 50 * 1024 * 1024) {
                    log.warn("파일 크기 초과: {} ({}bytes)", originalFileName, part.getSize());
                    continue;
                }

                String uploadPath = UploadFiles.upload(BASE_DIR, part);
                PostAttachmentVO attach = PostAttachmentVO.of(part, bno, uploadPath);
                postMapper.createAttachment(attach);

                log.info("파일 업로드 성공: {} -> {}", originalFileName, uploadPath);

            } catch (IOException e) {
                log.error("파일 업로드 실패: {}", part.getOriginalFilename(), e);
                throw new UploadFailException(ResponseCode.FILE_UPLOAD_FAIL);
            }
        }
    }

    private Long getCurrentUserIdAsLong() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberMapper.getMemberIdByEmail(email);
    }

    private void validateTags(String categoryTag, String productTag) {
        if (categoryTag != null && !CategoryTag.isValidCode(categoryTag)) {
            throw new InvalidTagException(ResponseCode.INVALID_CATEGORY_TAG);
        }

        if (productTag != null && !ProductTag.isValidCode(productTag)) {
            throw new InvalidTagException(ResponseCode.INVALID_PRODUCT_TAG);
        }
    }
}
