package org.scoula.community.scrap.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.auth.exception.AccessDeniedException;
import org.scoula.community.post.dto.PostListResponseDTO;
import org.scoula.community.scrap.dto.ScrapCountResponseDTO;
import org.scoula.community.scrap.dto.ScrapResponseDTO;
import org.scoula.community.scrap.service.ScrapService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scraps")
@RequiredArgsConstructor
@Log4j2
@Api(tags = "스크랩 API")
public class ScrapApiController {
    private final ScrapService scrapService;

    @ApiOperation(value = "스크랩 토글", notes = "게시글 스크랩을 추가하거나 해제합니다.")
    @PostMapping("/posts/{postId}")
    public ApiResponse<ScrapResponseDTO> toggleScrap(@PathVariable Long postId) {
        ScrapResponseDTO response = scrapService.toggleScrap(postId);
        if (response.isScraped()) {
            return ApiResponse.success(ResponseCode.SCRAP_ADD_SUCCESS, response);
        } else {
            return ApiResponse.success(ResponseCode.SCRAP_REMOVE_SUCCESS, response);
        }
    }

    @ApiOperation(value = "내 스크랩 목록 조회", notes = "현재 사용자가 스크랩한 게시글 목록을 조회합니다.")
    @GetMapping("/my")
    public ApiResponse<List<PostListResponseDTO>> getMyScrapList(@AuthenticationPrincipal UserDetails user) {
        if (user == null) {
            throw new AccessDeniedException(ResponseCode.UNAUTHORIZED_USER);
        }
        return ApiResponse.success(ResponseCode.SCRAP_LIST_SUCCESS, scrapService.getMyScrapList());
    }

    @ApiOperation(value = "게시글 스크랩 수 조회", notes = "특정 게시글의 스크랩 수를 조회합니다.")
    @GetMapping("/posts/{postId}/count")
    public ApiResponse<ScrapCountResponseDTO> getScrapCount(@PathVariable Long postId) {
        return ApiResponse.success(ResponseCode.SCRAP_COUNT_SUCCESS, scrapService.getScrapCount(postId));
    }
}