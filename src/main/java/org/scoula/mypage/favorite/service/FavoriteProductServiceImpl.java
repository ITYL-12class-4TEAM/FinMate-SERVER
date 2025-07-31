package org.scoula.mypage.favorite.service;

import lombok.RequiredArgsConstructor;
import org.scoula.mypage.favorite.exception.*;
import org.scoula.mypage.util.SecurityUtil;
import org.scoula.response.ResponseCode;
import org.scoula.mypage.favorite.dto.FavoriteProductResponse;
import org.scoula.mypage.favorite.dto.PopularFavoriteGroupResponse;
import org.scoula.mypage.favorite.dto.SubcategoryResponse;
import org.scoula.mypage.favorite.mapper.FavoriteProductMapper;
import org.scoula.mypage.favorite.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteProductServiceImpl implements FavoriteProductService {
    private final FavoriteProductMapper favoriteProductMapper;
    private final ProductMapper productMapper;
    private final SecurityUtil securityUtil;

    /**
     * 즐겨찾기 추가
     */
    public void addFavorite(Long productId, Integer saveTrm, String rsrvType) {
        Long memberId = securityUtil.getCurrentUserIdAsLong();

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
            throw new DatabaseOperationException(ResponseCode.DATABASE_ERROR);
        }
    }

    /**
     * 즐겨찾기 삭제
     */
    public void removeFavorite(Long productId) {
        Long memberId = securityUtil.getCurrentUserIdAsLong();

        // 즐겨찾기에 등록되어 있는지 확인
        if (!favoriteProductMapper.existsByMemberIdAndProductId(memberId, productId)) {
            throw new FavoriteNotFoundException(ResponseCode.FAVORITE_NOT_FOUND);
        }

        try {
            favoriteProductMapper.deleteFavorite(memberId, productId);
            favoriteProductMapper.decreaseWishlistCount(productId);
        } catch (Exception e) {
            throw new DatabaseOperationException(ResponseCode.DATABASE_ERROR);
        }
    }

    /**
     * 즐겨찾기 목록 조회
     */
    public List<FavoriteProductResponse> getFavorites() {
        Long memberId = securityUtil.getCurrentUserIdAsLong();

        try {
            List<FavoriteProductResponse> favorites = favoriteProductMapper.selectFavoritesByMemberId(memberId);

            // 연금 상품에 대한 추가 정보 설정
            processPensionProducts(favorites);

            return favorites;
        } catch (Exception e) {
            throw new FavoriteServiceException(ResponseCode.FAVORITE_READ_FAILED);
        }
    }

    /**
     * 특정 상품이 즐겨찾기에 등록되어 있는지 확인
     */
    public boolean isFavorite(Long productId) {
        Long memberId = securityUtil.getCurrentUserIdAsLong();

        // 상품 존재 여부 확인
        if (!productMapper.existsById(productId)) {
            throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
        }

        try {
            return favoriteProductMapper.existsByMemberIdAndProductId(memberId, productId);
        } catch (Exception e) {
            throw new FavoriteServiceException(ResponseCode.FAVORITE_CHECK_FAILED);
        }
    }

    /**
     * 카테고리별 인기 관심상품 조회
     * @param days 최근 며칠간의 데이터를 기준으로 인기 상품을 조회할지 설정
     * @return 카테고리별 인기 관심상품 목록
     */
    public List<PopularFavoriteGroupResponse> getPopularFavoritesByCategory(int days) {
        validateDaysParameter(days);

        try {
            List<SubcategoryResponse> subcategories = productMapper.getAllSubcategories();

            return subcategories.stream()
                    .map(sub -> createPopularFavoriteGroup(sub, days))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new FavoriteServiceException(ResponseCode.POPULAR_FAVORITE_READ_FAILED);
        }
    }

    /**
     * 연금 상품에 대한 추가 정보 처리
     */
    private void processPensionProducts(List<FavoriteProductResponse> favorites) {
        try {
            for (FavoriteProductResponse dto : favorites) {
                if ("연금".equals(dto.getCategoryName())) {
                    BigDecimal pensionRate = favoriteProductMapper.selectPensionRateByProductId(dto.getProductId());
                    dto.setBaseRate(pensionRate);
                    dto.setMaxRate(null);
                }
            }
        } catch (Exception e) {
            throw new FavoriteServiceException(ResponseCode.PENSION_RATE_PROCESSING_FAILED);
        }
    }

    /**
     * days 파라미터 유효성 검증
     */
    private void validateDaysParameter(int days) {
        if (days <= 0) {
            throw new ValidationException(ResponseCode.INVALID_DAYS_RANGE_MIN);
        }
        if (days > 365) {
            throw new ValidationException(ResponseCode.INVALID_DAYS_RANGE_MAX);
        }
    }

    /**
     * 인기 즐겨찾기 그룹 생성
     */
    private PopularFavoriteGroupResponse createPopularFavoriteGroup(SubcategoryResponse sub, int days) {
        try {
            List<FavoriteProductResponse> products =
                    favoriteProductMapper.selectPopularFavorites(sub.getSubcategoryId(), days);

            PopularFavoriteGroupResponse dto = new PopularFavoriteGroupResponse();
            dto.setSubcategoryId(sub.getSubcategoryId());
            dto.setSubcategoryName(sub.getSubcategoryName());
            dto.setProducts(products);
            return dto;
        } catch (Exception e) {
            throw new DatabaseOperationException(ResponseCode.DATABASE_ERROR);
        }
    }
}