package org.scoula.mypage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.mypage.dto.FavoriteProductDto;
import org.scoula.mypage.dto.ViewedProductResponseDTO;

import java.util.List;

@Mapper
public interface ViewedProductMapper {

    // 최근 본 상품 추가
    void insertViewedProduct(@Param("memberId") Long memberId, @Param("productId") Long productId, @Param("saveTrm") Integer saveTrm, @Param("rsrvType") String rsrvType);

    // 기존 동일 상품 삭제 (중복 방지용)
    void deleteExistingViewedProduct(@Param("memberId") Long memberId, @Param("productId") Long productId, @Param("saveTrm") Integer saveTrm, @Param("rsrvType") String rsrvType);

    // 최근 본 상품 목록 조회
    List<ViewedProductResponseDTO> selectRecentViewedProducts(@Param("memberId") Long memberId);

    // 특정 상품의 최근 본 기록 삭제
    void deleteViewedProduct(@Param("memberId") Long memberId, @Param("productId") Long productId);

    // 모든 최근 본 상품 기록 삭제
    void deleteAllViewedProducts(@Param("memberId") Long memberId);

}
