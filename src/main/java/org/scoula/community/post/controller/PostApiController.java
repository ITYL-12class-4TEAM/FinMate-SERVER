package org.scoula.community.post.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.File;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.common.util.UploadFiles;
import org.scoula.community.post.domain.PostAttachmentVO;
import org.scoula.community.post.dto.PostCreateRequestDTO;
import org.scoula.community.post.dto.PostDetailsResponseDTO;
import org.scoula.community.post.dto.PostListResponseDTO;
import org.scoula.community.post.dto.PostUpdateRequestDTO;
import org.scoula.community.post.service.PostService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Log4j2
@Api(tags = "게시글 API")
public class PostApiController {

    private final PostService postService;

    @ApiOperation(value = "게시글 단건 조회", notes = "postId에 해당하는 게시글 상세 정보를 반환합니다.")
    @GetMapping("/{postId}")
    public ApiResponse<PostDetailsResponseDTO> get(@PathVariable Long postId) {
        return ApiResponse.success(ResponseCode.POST_DETAILS_SUCCESS, postService.get(postId));
    }


    @ApiOperation(value = "게시글 생성", notes = "새 게시글을 등록합니다. 파일 첨부 가능합니다.")
    @PostMapping(value = "")
    public ApiResponse<PostDetailsResponseDTO> create(@RequestBody PostCreateRequestDTO postCreateRequestDTO){
        PostDetailsResponseDTO created = postService.create(postCreateRequestDTO);
        return ApiResponse.success(ResponseCode.POST_CREATE_SUCCESS, created);
    }

    @ApiOperation(value = "게시판별 게시글 리스트 조회", notes = "특정 게시판의 게시글을 최신 등록일 기준으로 내림차순 조회합니다.")
    @GetMapping("/board/{boardId}")
    public ApiResponse<List<PostListResponseDTO>> getListByBoard(
            @ApiParam(value = "게시판 ID", required = true, example = "1")
            @PathVariable Long boardId) {
        return ApiResponse.success(ResponseCode.POST_LIST_SUCCESS, postService.getListByBoard(boardId));
    }

    @ApiOperation(value = "게시글 수정", notes = "기존 게시글을 수정합니다. 추가 파일 첨부 가능합니다.")
    @PutMapping(value = "/{postId}")
    public ApiResponse<PostDetailsResponseDTO> update(
            @PathVariable Long postId, @RequestBody PostUpdateRequestDTO postUpdateRequestDTO) {
        PostDetailsResponseDTO updated = postService.update(postId, postUpdateRequestDTO);
        return ApiResponse.success(ResponseCode.POST_UPDATE_SUCCESS, updated);
    }

    @ApiOperation(value = "게시글 삭제", notes = "postId에 해당하는 게시글을 삭제합니다.")
    @DeleteMapping("/{postId}")
    public ApiResponse<Void> delete(@PathVariable Long postId) {
        postService.delete(postId);
        return ApiResponse.success(ResponseCode.POST_DELETE_SUCCESS);
    }

    @ApiOperation(value = "첨부파일 다운로드", notes = "첨부파일을 다운로드합니다.")
    @GetMapping("/attachment/{no}/download")
    public ApiResponse<?> downloadAttachment(@PathVariable Long no, HttpServletResponse response) throws Exception {
        PostAttachmentVO attachmentVO = postService.getAttachment(no);
        File file = new File(attachmentVO.getPath());
        UploadFiles.download(response, file, attachmentVO.getFilename());
        return ApiResponse.success(ResponseCode.ATTACHMENT_DOWNLOAD_SUCCESS);
    }

    @ApiOperation(value = "첨부파일 삭제", notes = "첨부파일을 삭제합니다.")
    @DeleteMapping("/attachment/{no}")
    public ApiResponse<Void> deleteAttachment(@PathVariable Long no) {
        postService.deleteAttachment(no);
        return ApiResponse.success(ResponseCode.ATTACHMENT_DELETE_SUCCESS);
    }
}
