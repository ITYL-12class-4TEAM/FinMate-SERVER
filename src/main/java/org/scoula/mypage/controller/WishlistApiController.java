package org.scoula.mypage.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.mypage.dto.FavoriteProductDto;
import org.scoula.mypage.dto.FavoriteRequestDTO;
import org.scoula.mypage.service.FavoriteProductService;
import org.springframework.http.ResponseEntity;
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
        favoriteProductService.addFavorite(request.getMemberId(), request.getProductId());
        return ResponseEntity.ok().build();
    }

    // 관심상품 삭제
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFavorite(@RequestParam Long memberId, @PathVariable Long productId) {
        favoriteProductService.removeFavorite(memberId, productId);
        return ResponseEntity.ok().build();
    }

    // 관심상품 목록 조회
    @GetMapping
    public ResponseEntity<List<FavoriteProductDto>> getFavorites(@RequestParam Long memberId) {
        return ResponseEntity.ok(favoriteProductService.getFavorites(memberId));
    }

    // 관심상품 여부 확인
    @GetMapping("/status/{productId}")
    public ResponseEntity<Boolean> checkFavorite(@RequestParam Long memberId, @PathVariable Long productId) {
        return ResponseEntity.ok(favoriteProductService.isFavorite(memberId, productId));
    }
}
