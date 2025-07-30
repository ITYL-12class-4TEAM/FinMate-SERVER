package org.scoula.mypage.favorite.service;

import lombok.RequiredArgsConstructor;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.mypage.favorite.dto.FavoriteProductResponse;
import org.scoula.mypage.favorite.dto.PopularFavoriteGroupResponse;
import org.scoula.mypage.favorite.dto.SubcategoryResponse;
import org.scoula.mypage.favorite.mapper.FavoriteProductMapper;
import org.scoula.mypage.favorite.mapper.ProductMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteProductServiceImpl implements FavoriteProductService {
    private final FavoriteProductMapper favoriteProductMapper;
    private final ProductMapper productMapper;
    private final MemberMapper memberMapper;

    /**
     * 즐겨찾기 추가
     */
    public void addFavorite(Long productId, Integer saveTrm, String rsrvType) {
        Long memberId = getCurrentUserIdAsLong();
        favoriteProductMapper.insertFavorite(memberId, productId, saveTrm, rsrvType);
        favoriteProductMapper.increaseWishlistCount(productId);
    }

    /**
     * 즐겨찾기 삭제
     */
    public void removeFavorite(Long productId) {
        Long memberId = getCurrentUserIdAsLong();

        favoriteProductMapper.deleteFavorite(memberId, productId);
        favoriteProductMapper.decreaseWishlistCount(productId);
    }

    /**
     * 즐겨찾기 목록 조회
     */
    public List<FavoriteProductResponse> getFavorites() {
        Long memberId = getCurrentUserIdAsLong();

        List<FavoriteProductResponse> favorites = favoriteProductMapper.selectFavoritesByMemberId(memberId);

        for (FavoriteProductResponse dto : favorites) {
            if ("연금".equals(dto.getCategoryName())) {
                BigDecimal pensionRate = favoriteProductMapper.selectPensionRateByProductId(dto.getProductId());
                dto.setBaseRate(pensionRate);
                dto.setMaxRate(null);
            }
        }

        return favorites;
    }


    /**
     * 특정 상품이 즐겨찾기에 등록되어 있는지 확인
     */
    public boolean isFavorite(Long productId) {
        Long memberId = getCurrentUserIdAsLong();

        return favoriteProductMapper.existsByMemberIdAndProductId(memberId, productId);
    }

    /**
     * 카테고리별 인기 관심상품 조회
     * @param days 최근 며칠간의 데이터를 기준으로 인기 상품을 조회할지 설정
     * @return 카테고리별 인기 관심상품 목록
     */
    public List<PopularFavoriteGroupResponse> getPopularFavoritesByCategory(int days) {
        List<SubcategoryResponse> subcategories = productMapper.getAllSubcategories();

        return subcategories.stream()
                .map(sub -> {
                    List<FavoriteProductResponse> products =
                            favoriteProductMapper.selectPopularFavorites(sub.getSubcategoryId(), days);
                    PopularFavoriteGroupResponse dto = new PopularFavoriteGroupResponse();
                    dto.setSubcategoryId(sub.getSubcategoryId());
                    dto.setSubcategoryName(sub.getSubcategoryName());
                    dto.setProducts(products);
                    return dto;
                })
                .collect(Collectors.toList());
    }
    private Long getCurrentUserIdAsLong() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberMapper.getMemberIdByEmail(email);
    }
}
