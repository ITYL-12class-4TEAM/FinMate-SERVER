package org.scoula.community.post.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.File;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.auth.exception.AccessDeniedException;
import org.scoula.common.util.UploadFiles;
import org.scoula.community.post.domain.PostAttachmentVO;
import org.scoula.community.post.dto.PageRequestDTO;
import org.scoula.community.post.dto.PageResponseDTO;
import org.scoula.community.post.dto.PostCreateRequestDTO;
import org.scoula.community.post.dto.PostDetailsResponseDTO;
import org.scoula.community.post.dto.PostListResponseDTO;
import org.scoula.community.post.dto.PostUpdateRequestDTO;
import org.scoula.community.post.service.PostService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @ApiOperation(value = "전체 게시글 페이징 조회", notes = "전체 게시글을 페이징 처리하여 조회합니다.")
    @GetMapping("/paging")
    public ApiResponse<PageResponseDTO<PostListResponseDTO>> getListWithPaging(
            @ApiParam(value = "페이지 번호 (1부터 시작)", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "페이지 크기", example = "5")
            @RequestParam(defaultValue = "5") int size) {

        try {
            log.info("페이징 요청 - page: {}, size: {}", page, size);

            PageRequestDTO pageRequest = new PageRequestDTO();
            pageRequest.setPage(page);
            pageRequest.setSize(size);

            log.info("페이지 요청 객체 - page: {}, size: {}, offset: {}",
                    pageRequest.getPage(), pageRequest.getSize(), pageRequest.getOffset());

            PageResponseDTO<PostListResponseDTO> result = postService.getListWithPaging(pageRequest);

            log.info("페이징 결과 - 총 {}개 중 {}개 조회", result.getTotalElements(), result.getContent().size());

            return ApiResponse.success(ResponseCode.POST_LIST_SUCCESS, result);

        } catch (Exception e) {
            log.error("페이징 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e; // 에러를 다시 던져서 상세 정보 확인
        }
    }

    @ApiOperation(value = "게시판별 게시글 페이징 조회", notes = "특정 게시판의 게시글을 페이징 처리하여 조회합니다.")
    @GetMapping("/board/{boardId}/paging")
    public ApiResponse<PageResponseDTO<PostListResponseDTO>> getListByBoardWithPaging(
            @ApiParam(value = "게시판 ID", required = true, example = "1")
            @PathVariable Long boardId,
            @ApiParam(value = "페이지 번호 (1부터 시작)", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "페이지 크기", example = "5")
            @RequestParam(defaultValue = "5") int size) {

        PageRequestDTO pageRequest = new PageRequestDTO();
        pageRequest.setPage(page);
        pageRequest.setSize(size);

        return ApiResponse.success(ResponseCode.POST_LIST_SUCCESS,
                postService.getListByBoardWithPaging(boardId, pageRequest));
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

    @ApiOperation(value = "내가 쓴 글 조회", notes = "현재 로그인한 사용자가 작성한 게시글 목록을 조회합니다.")
    @GetMapping("/my")
    public ApiResponse<List<PostListResponseDTO>> getMyPosts() {
        return ApiResponse.success(ResponseCode.POST_LIST_SUCCESS, postService.getMyPosts());
    }
    @ApiOperation(value = "내가 쓴 글 페이징 조회", notes = "현재 로그인한 사용자가 작성한 게시글을 페이징 처리하여 조회합니다.")
    @GetMapping("/my/paging")
    public ApiResponse<PageResponseDTO<PostListResponseDTO>> getMyPostsWithPaging(
            @ApiParam(value = "페이지 번호 (1부터 시작)", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "페이지 크기", example = "5")
            @RequestParam(defaultValue = "5") int size) {

        PageRequestDTO pageRequest = new PageRequestDTO();
        pageRequest.setPage(page);
        pageRequest.setSize(size);

        return ApiResponse.success(ResponseCode.POST_LIST_SUCCESS, postService.getMyPostsWithPaging(pageRequest));
    }
    @ApiOperation(value = "게시판별 핫게시물 조회 (전날 기준)", notes = "전날 작성된 게시물 중 좋아요 순으로 정렬된 핫게시물을 조회합니다.")
    @GetMapping("/board/{boardId}/hot")
    public ApiResponse<List<PostListResponseDTO>> getHotPostsByBoard(
            @ApiParam(value = "게시판 ID", required = true, example = "1")
            @PathVariable Long boardId) {
        return ApiResponse.success(ResponseCode.POST_LIST_SUCCESS, postService.getHotPostsByBoard(boardId));
    }

    @ApiOperation(value = "전체 핫게시물 조회 (전날 기준)", notes = "전날 작성된 모든 게시물 중 좋아요 순으로 정렬된 핫게시물을 조회합니다.")
    @GetMapping("/hot")
    public ApiResponse<List<PostListResponseDTO>> getAllHotPosts() {
        return ApiResponse.success(ResponseCode.POST_LIST_SUCCESS, postService.getAllHotPosts());
    }
}
