package org.scoula.mypage.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.mypage.dto.FavoriteProductDto;
import org.scoula.mypage.dto.FavoriteRequestDTO;
import org.scoula.mypage.dto.PopularProductGroupDto;
import org.scoula.mypage.service.FavoriteProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistApiController {

    private final FavoriteProductService favoriteProductService;

    // 관심상품 등록
    @PostMapping
    public ResponseEntity<Void> addFavorite(@RequestBody FavoriteRequestDTO request) {
        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        favoriteProductService.addFavorite(memberId, request.getProductId());
        return ResponseEntity.ok().build();
    }

    // 관심상품 삭제
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long productId) {
        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        favoriteProductService.removeFavorite(memberId, productId);
        return ResponseEntity.ok().build();
    }


    // 관심상품 목록 조회
    @GetMapping
    public ResponseEntity<List<FavoriteProductDto>> getFavorites() {
        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(favoriteProductService.getFavorites(memberId));
    }

    // 관심상품 여부 확인
    @GetMapping("/status/{productId}")
    public ResponseEntity<Boolean> checkFavorite(@PathVariable Long productId) {
        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(favoriteProductService.isFavorite(memberId, productId));
    }

    // 카테고리별 인기 관심상품 조회
    @GetMapping("/populary")
    public ResponseEntity<List<PopularProductGroupDto>> getPopularFavoritesByCategory(
            @RequestParam(defaultValue = "30") int days) {
        List<PopularProductGroupDto> result = favoriteProductService.getPopularFavoritesByCategory(days);
        return ResponseEntity.ok(result);
    }
}
