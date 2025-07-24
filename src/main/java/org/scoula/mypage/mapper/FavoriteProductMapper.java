package org.scoula.mypage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.mypage.dto.FavoriteProductDto;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface FavoriteProductMapper {
    // 즐겨찾기 추가
    void insertFavorite(@Param("memberId") Long memberId, @Param("productId") Long productId, @Param("saveTrm") Integer saveTrm, @Param("rsrvType") String rsrvType);
    // 즐겨찾기 추가 시 위시리스트 카운트 증가
    void increaseWishlistCount(@Param("productId") Long productId);

    // 즐겨찾기 삭제
    void deleteFavorite(@Param("memberId") Long memberId, @Param("productId") Long productId);
    // 즐겨찾기 삭제 시 위시리스트 카운트 감소
    void decreaseWishlistCount(@Param("productId") Long productId);

    // 특정 회원의 즐겨찾기 목록 조회
    List<FavoriteProductDto> selectFavoritesByMemberId(@Param("memberId") Long memberId);

    // 특정 회원의 즐겨찾기 목록에서 상품이 존재하는지 확인
    boolean existsByMemberIdAndProductId(@Param("memberId") Long memberId, @Param("productId") Long productId);

    // 카테고리별 인기 관심상품 조회
    List<FavoriteProductDto> selectPopularFavorites(@Param("subcategoryId") Long subcategoryId, @Param("days") int days);

    // 특정 상품의 연금 금리 조회
    BigDecimal selectPensionRateByProductId(@Param("productId") Long productId);

}
