package org.scoula.mypage.favorite.service;

import org.scoula.mypage.favorite.dto.FavoriteProductResponse;
import org.scoula.mypage.favorite.dto.PopularFavoriteGroupResponse;

import java.util.List;

public interface FavoriteProductService {
    // 즐겨찾기 추가
    void addFavorite(Long productId, Integer saveTrm, String intrRateType, String rsrvType);

    // 즐겨찾기 삭제
    void removeFavorite(Long productId);

    // 특정 회원의 즐겨찾기 목록 조회
    List<FavoriteProductResponse> getFavorites();

    // 특정 회원의 즐겨찾기 목록에서 상품이 존재하는지 확인
    boolean isFavorite(Long productId);

    // 카테고리별 인기 관심상품 조회
    List<PopularFavoriteGroupResponse> getPopularFavoritesByCategory(int days);
}
