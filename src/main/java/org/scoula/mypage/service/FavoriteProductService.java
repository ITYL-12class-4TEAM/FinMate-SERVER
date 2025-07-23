package org.scoula.mypage.service;

import lombok.RequiredArgsConstructor;
import org.scoula.mypage.dto.FavoriteProductDto;
import org.scoula.mypage.dto.PopularProductGroupDto;
import org.scoula.mypage.dto.SubcategoryDto;
import org.scoula.mypage.mapper.FavoriteProductMapper;
import org.scoula.mypage.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteProductService {

    private final FavoriteProductMapper favoriteProductMapper;
    private final ProductMapper productMapper;

    /**
     * 즐겨찾기 추가
     */
    public void addFavorite(Long memberId, Long productId) {
        favoriteProductMapper.insertFavorite(memberId, productId);
        favoriteProductMapper.increaseWishlistCount(productId);

    }

    /**
     * 즐겨찾기 삭제
     */
    public void removeFavorite(Long memberId, Long productId) {
        favoriteProductMapper.deleteFavorite(memberId, productId);
        favoriteProductMapper.decreaseWishlistCount(productId);
    }

    /**
     * 즐겨찾기 목록 조회
     */
    public List<FavoriteProductDto> getFavorites(Long memberId) {
        return favoriteProductMapper.selectFavoritesByMemberId(memberId);
    }

    /**
     * 특정 상품이 즐겨찾기에 등록되어 있는지 확인
     */
    public boolean isFavorite(Long memberId, Long productId) {
        return favoriteProductMapper.existsByMemberIdAndProductId(memberId, productId);
    }

    /**
     * 카테고리별 인기 관심상품 조회
     * @param days 최근 며칠간의 데이터를 기준으로 인기 상품을 조회할지 설정
     * @return 카테고리별 인기 관심상품 목록
     */
    public List<PopularProductGroupDto> getPopularFavoritesByCategory(int days) {
        List<SubcategoryDto> subcategories = productMapper.getAllSubcategories();

        return subcategories.stream()
                .map(sub -> {
                    List<FavoriteProductDto> products =
                            favoriteProductMapper.selectPopularFavorites(sub.getSubcategoryId(), days);
                    PopularProductGroupDto dto = new PopularProductGroupDto();
                    dto.setSubcategoryId(sub.getSubcategoryId());
                    dto.setSubcategoryName(sub.getSubcategoryName());
                    dto.setProducts(products);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}

