package org.scoula.mypage.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.mypage.dto.FavoriteProductDto;
import org.scoula.mypage.dto.ViewedProductRequestDTO;
import org.scoula.mypage.service.RecentViewedService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recent-viewed")
@RequiredArgsConstructor
public class RecentViewedApiController {

    private final RecentViewedService viewedProductService;

    // 최근 본 상품 저장
    @PostMapping
    public ResponseEntity<Void> saveRecentView(@RequestBody ViewedProductRequestDTO request) {
        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        viewedProductService.saveRecentView(memberId, request.getProductId());
        return ResponseEntity.ok().build();
    }

    // 최근 본 상품 목록 조회
    @GetMapping
    public ResponseEntity<List<FavoriteProductDto>> getRecentViews() {
        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(viewedProductService.getRecentViews(memberId));
    }

    // 특정 상품의 최근 본 기록 삭제
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteRecentView(@PathVariable Long productId) {
        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        viewedProductService.deleteRecentView(memberId, productId);
        return ResponseEntity.ok().build();
    }

    // 모든 최근 본 상품 기록 삭제
    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllRecentViews() {
        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        viewedProductService.deleteAllRecentViews(memberId);
        return ResponseEntity.ok().build();
    }
}
