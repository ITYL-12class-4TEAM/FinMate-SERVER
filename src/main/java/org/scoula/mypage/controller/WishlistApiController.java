package org.scoula.mypage.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.scoula.mypage.dto.FavoriteProductDto;
import org.scoula.mypage.dto.FavoriteRequestDTO;
import org.scoula.mypage.dto.PopularProductGroupDto;
import org.scoula.mypage.service.FavoriteProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "관심상품 API", description = "즐겨찾기(관심상품) 관련 기능을 제공합니다.")
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistApiController {

    private final FavoriteProductService favoriteProductService;

    @ApiOperation(value = "관심상품 등록", notes = "로그인한 사용자가 특정 상품을 관심상품으로 등록합니다.")
    @PostMapping
    public ResponseEntity<Void> addFavorite(
            @ApiParam(value = "관심상품 등록 요청 DTO", required = true)
            @RequestBody FavoriteRequestDTO request) {
        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        favoriteProductService.addFavorite(memberId, request.getProductId());
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "관심상품 삭제", notes = "관심상품으로 등록된 상품을 삭제합니다.")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFavorite(
            @ApiParam(value = "삭제할 상품 ID", required = true)
            @PathVariable Long productId) {
        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        favoriteProductService.removeFavorite(memberId, productId);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "관심상품 목록 조회", notes = "사용자가 등록한 모든 관심상품 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<FavoriteProductDto>> getFavorites() {
        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(favoriteProductService.getFavorites(memberId));
    }

    @ApiOperation(value = "관심상품 여부 확인", notes = "특정 상품이 사용자의 관심상품인지 여부를 반환합니다.")
    @GetMapping("/status/{productId}")
    public ResponseEntity<Boolean> checkFavorite(
            @ApiParam(value = "조회할 상품 ID", required = true)
            @PathVariable Long productId) {
        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(favoriteProductService.isFavorite(memberId, productId));
    }

    @ApiOperation(value = "인기 관심상품 조회", notes = "카테고리별로 최근 N일 간의 인기 관심상품을 조회합니다.")
    @GetMapping("/populary")
    public ResponseEntity<List<PopularProductGroupDto>> getPopularFavoritesByCategory(
            @ApiParam(value = "조회 기간(일 단위)", defaultValue = "30")
            @RequestParam(defaultValue = "30") int days) {
        List<PopularProductGroupDto> result = favoriteProductService.getPopularFavoritesByCategory(days);
        return ResponseEntity.ok(result);
    }
}
