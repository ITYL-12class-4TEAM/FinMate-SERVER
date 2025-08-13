package org.scoula.mypage.recentView.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.scoula.mypage.recentView.dto.RecentProductRequest;
import org.scoula.mypage.recentView.dto.RecentProductResponse;
import org.scoula.mypage.recentView.service.RecentViewedService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "최근 본 상품 API", description = "사용자의 최근 본 금융 상품 기록을 관리하는 API")
@RestController
@RequestMapping("/api/recent-viewed")
@RequiredArgsConstructor
public class RecentViewedApiController {

    private final RecentViewedService viewedProductService;

    @ApiOperation(value = "최근 본 상품 저장", notes = "상품 상세 페이지 접근 시 최근 본 상품으로 저장합니다.")
    @PostMapping
    public ApiResponse<Void> saveRecentView(
            @ApiParam(value = "최근 본 상품 등록 요청 DTO", required = true)
            @Valid @RequestBody RecentProductRequest request) {
        viewedProductService.saveRecentView(
                request.getProductId(),
                request.getSaveTrm(),
                request.getIntrRateType(),
                request.getRsrvType()
        );
        return ApiResponse.success(ResponseCode.RECENT_VIEW_CREATE_SUCCESS);
    }

    @ApiOperation(value = "최근 본 상품 목록 조회", notes = "로그인한 사용자의 최근 본 상품 목록을 최신순으로 조회합니다.")
    @GetMapping
    public ApiResponse<List<RecentProductResponse>> getRecentViews() {
        List<RecentProductResponse> recentView = viewedProductService.getRecentViews();
        return ApiResponse.success(ResponseCode.RECENT_VIEW_READ_SUCCESS, recentView);
    }

    @ApiOperation(value = "특정 상품의 최근 본 기록 삭제", notes = "지정한 상품 ID에 대한 최근 본 기록을 삭제합니다.")
    @DeleteMapping("/{productId}")
    public ApiResponse<Void> deleteRecentView(
            @ApiParam(value = "삭제할 상품 ID", required = true)
            @PathVariable Long productId) {
        viewedProductService.deleteRecentView(productId);
        return ApiResponse.success(ResponseCode.RECENT_VIEW_DELETE_SUCCESS);
    }

    @ApiOperation(value = "최근 본 상품 전체 삭제", notes = "로그인한 사용자의 모든 최근 본 상품 기록을 삭제합니다.")
    @DeleteMapping("/all")
    public ApiResponse<Void> deleteAllRecentViews() {
        viewedProductService.deleteAllRecentViews();
        return ApiResponse.success(ResponseCode.RECENT_VIEW_DELETE_ALL_SUCCESS);
    }
}
