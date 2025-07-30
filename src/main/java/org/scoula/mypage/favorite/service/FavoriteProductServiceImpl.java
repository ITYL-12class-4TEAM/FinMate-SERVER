package org.scoula.mypage.favorite.service;

import lombok.RequiredArgsConstructor;
import org.scoula.response.ResponseCode;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.mypage.favorite.dto.FavoriteProductResponse;
import org.scoula.mypage.favorite.dto.PopularFavoriteGroupResponse;
import org.scoula.mypage.favorite.dto.SubcategoryResponse;
import org.scoula.mypage.favorite.exception.FavoriteAlreadyExistsException;
import org.scoula.mypage.favorite.exception.FavoriteNotFoundException;
import org.scoula.mypage.favorite.exception.ProductNotFoundException;
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

        // 상품 존재 여부 확인
        if (!productMapper.existsById(productId)) {
            throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
        }

        // 이미 즐겨찾기에 등록되어 있는지 확인
        if (favoriteProductMapper.existsByMemberIdAndProductId(memberId, productId)) {
            throw new FavoriteAlreadyExistsException(ResponseCode.FAVORITE_ALREADY_EXISTS);
        }

        try {
            favoriteProductMapper.insertFavorite(memberId, productId, saveTrm, rsrvType);
            favoriteProductMapper.increaseWishlistCount(productId);
        } catch (Exception e) {
            throw new RuntimeException("즐겨찾기 추가 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 즐겨찾기 삭제
     */
    public void removeFavorite(Long productId) {
        Long memberId = getCurrentUserIdAsLong();

        // 즐겨찾기에 등록되어 있는지 확인
        if (!favoriteProductMapper.existsByMemberIdAndProductId(memberId, productId)) {
            throw new FavoriteNotFoundException(ResponseCode.FAVORITE_NOT_FOUND);
        }

        try {
            favoriteProductMapper.deleteFavorite(memberId, productId);
            favoriteProductMapper.decreaseWishlistCount(productId);
        } catch (Exception e) {
            throw new RuntimeException("즐겨찾기 삭제 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 즐겨찾기 목록 조회
     */
    public List<FavoriteProductResponse> getFavorites() {
        Long memberId = getCurrentUserIdAsLong();

        try {
            List<FavoriteProductResponse> favorites = favoriteProductMapper.selectFavoritesByMemberId(memberId);

            for (FavoriteProductResponse dto : favorites) {
                if ("연금".equals(dto.getCategoryName())) {
                    BigDecimal pensionRate = favoriteProductMapper.selectPensionRateByProductId(dto.getProductId());
                    dto.setBaseRate(pensionRate);
                    dto.setMaxRate(null);
                }
            }

            return favorites;
        } catch (Exception e) {
            throw new RuntimeException("즐겨찾기 목록 조회 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 특정 상품이 즐겨찾기에 등록되어 있는지 확인
     */
    public boolean isFavorite(Long productId) {
        Long memberId = getCurrentUserIdAsLong();

        // 상품 존재 여부 확인
        if (!productMapper.existsById(productId)) {
            throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
        }

        try {
            return favoriteProductMapper.existsByMemberIdAndProductId(memberId, productId);
        } catch (Exception e) {
            throw new RuntimeException("즐겨찾기 상태 확인 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 카테고리별 인기 관심상품 조회
     * @param days 최근 며칠간의 데이터를 기준으로 인기 상품을 조회할지 설정
     * @return 카테고리별 인기 관심상품 목록
     */
    public List<PopularFavoriteGroupResponse> getPopularFavoritesByCategory(int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("조회 기간은 1일 이상이어야 합니다.");
        }

        try {
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
        } catch (Exception e) {
            throw new RuntimeException("인기 관심상품 조회 중 오류가 발생했습니다.", e);
        }
    }

    private Long getCurrentUserIdAsLong() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Long memberId = memberMapper.getMemberIdByEmail(email);

            if (memberId == null) {
                throw new RuntimeException("사용자 정보를 찾을 수 없습니다.");
            }

            return memberId;
        } catch (Exception e) {
            throw new RuntimeException("사용자 인증 정보를 가져오는데 실패했습니다.", e);
        }
    }
}