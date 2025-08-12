package org.scoula.mypage.favorite.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.scoula.mypage.favorite.dto.FavoriteProductResponse;
import org.scoula.mypage.favorite.dto.FavoriteProductRequest;
import org.scoula.mypage.favorite.dto.PopularFavoriteGroupResponse;
import org.scoula.mypage.favorite.service.FavoriteProductService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "관심상품 API", description = "즐겨찾기(관심상품) 관련 기능을 제공합니다.")
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistApiController {

    private final FavoriteProductService favoriteProductService;

    @ApiOperation(value = "관심상품 등록", notes = "로그인한 사용자가 특정 상품을 관심상품으로 등록합니다.")
    @PostMapping
    public ApiResponse<Void> addFavorite(
            @ApiParam(value = "관심상품 등록 요청 DTO", required = true)
            @Valid @RequestBody FavoriteProductRequest request) {
        favoriteProductService.addFavorite(
                request.getProductId(),
                request.getSaveTrm(),
                request.getIntrRateType(),
                request.getRsrvType()
        );
        return ApiResponse.success(ResponseCode.FAVORITE_CREATE_SUCCESS);
    }

    @ApiOperation(value = "관심상품 삭제", notes = "관심상품으로 등록된 상품을 삭제합니다.")
    @DeleteMapping("/{productId}")
    public ApiResponse<Void> removeFavorite(
            @ApiParam(value = "삭제할 상품 ID", required = true)
            @PathVariable Long productId) {
        favoriteProductService.removeFavorite(productId);
        return ApiResponse.success(ResponseCode.FAVORITE_DELETE_SUCCESS);
    }

    @ApiOperation(value = "관심상품 목록 조회", notes = "사용자가 등록한 모든 관심상품 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<List<FavoriteProductResponse>> getFavorites() {
        List<FavoriteProductResponse> favorites = favoriteProductService.getFavorites();
        return ApiResponse.success(ResponseCode.FAVORITE_READ_SUCCESS, favorites);
    }

    @ApiOperation(value = "관심상품 여부 확인", notes = "특정 상품이 사용자의 관심상품인지 여부를 반환합니다.")
    @GetMapping("/status/{productId}")
    public ApiResponse<Boolean> checkFavorite(
            @ApiParam(value = "조회할 상품 ID", required = true)
            @PathVariable Long productId) {
        Boolean isFavorite = favoriteProductService.isFavorite(productId);
        return ApiResponse.success(ResponseCode.FAVORITE_STATUS_CHECK_SUCCESS, isFavorite);
    }

    @ApiOperation(value = "인기 관심상품 조회", notes = "카테고리별로 최근 N일 간의 인기 관심상품을 조회합니다.")
    @GetMapping("/populary")
    public ApiResponse<List<PopularFavoriteGroupResponse>> getPopularFavoritesByCategory(
            @ApiParam(value = "조회 기간(일 단위)", defaultValue = "30")
            @RequestParam(defaultValue = "30") int days) {
        List<PopularFavoriteGroupResponse> result = favoriteProductService.getPopularFavoritesByCategory(days);
        return ApiResponse.success(ResponseCode.POPULAR_FAVORITE_READ_SUCCESS, result);
    }
}
