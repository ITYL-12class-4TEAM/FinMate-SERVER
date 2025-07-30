package org.scoula.mypage.recentView.controller;

import io.swagger.annotations.*;
        import lombok.RequiredArgsConstructor;
import org.scoula.mypage.recentView.dto.RecentProductRequest;
import org.scoula.mypage.recentView.dto.RecentProductResponse;
import org.scoula.mypage.recentView.service.RecentViewedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


        import java.util.List;

@Api(tags = "최근 본 상품 API", description = "사용자의 최근 본 금융 상품 기록을 관리하는 API")
@RestController
@RequestMapping("/api/recent-viewed")
@RequiredArgsConstructor
public class RecentViewedApiController {

    private final RecentViewedService viewedProductService;

    @ApiOperation(value = "최근 본 상품 저장", notes = "상품 상세 페이지 접근 시 최근 본 상품으로 저장합니다.")
    @PostMapping
    public ResponseEntity<Void> saveRecentView(
            @ApiParam(value = "최근 본 상품 등록 요청 DTO", required = true)
            @RequestBody RecentProductRequest request) {
        viewedProductService.saveRecentView(request.getProductId(), request.getSaveTrm(), request.getRsrvType());
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "최근 본 상품 목록 조회", notes = "로그인한 사용자의 최근 본 상품 목록을 최신순으로 조회합니다.")
    @GetMapping
    public ResponseEntity<List<RecentProductResponse>> getRecentViews() {
        List<RecentProductResponse> recentView = viewedProductService.getRecentViews(); // saveTrm 없이
        return ResponseEntity.ok(recentView);
    }

    @ApiOperation(value = "특정 상품의 최근 본 기록 삭제", notes = "지정한 상품 ID에 대한 최근 본 기록을 삭제합니다.")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteRecentView(
            @ApiParam(value = "삭제할 상품 ID", required = true)
            @PathVariable Long productId) {
        viewedProductService.deleteRecentView( productId);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "최근 본 상품 전체 삭제", notes = "로그인한 사용자의 모든 최근 본 상품 기록을 삭제합니다.")
    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllRecentViews() {
        viewedProductService.deleteAllRecentViews();
        return ResponseEntity.ok().build();
    }
}
