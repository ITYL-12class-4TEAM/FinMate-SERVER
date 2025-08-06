package org.scoula.mypage.recentView.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.mypage.recentView.dto.RecentProductResponse;

import java.util.List;

@Mapper
public interface ViewedProductMapper {

    // 최근 본 상품 추가
    void insertViewedProduct(@Param("memberId") Long memberId, @Param("productId") Long productId, @Param("saveTrm") Integer saveTrm,@Param("intrRateType") String intrRateType, @Param("rsrvType") String rsrvType);

    // 기존 동일 상품 삭제 (중복 방지용)
    void deleteExistingViewedProduct(@Param("memberId") Long memberId, @Param("productId") Long productId, @Param("saveTrm") Integer saveTrm, @Param("intrRateType") String intrRateType, @Param("rsrvType") String rsrvType);

    // 최근 본 상품 목록 조회
    List<RecentProductResponse> selectRecentViewedProducts(@Param("memberId") Long memberId);

    // 특정 상품의 최근 본 기록 삭제 (삭제된 레코드 수 반환)
    int deleteViewedProduct(@Param("memberId") Long memberId, @Param("productId") Long productId);

    // 모든 최근 본 상품 기록 삭제 (삭제된 레코드 수 반환)
    int deleteAllViewedProducts(@Param("memberId") Long memberId);

    // 특정 회원의 최근 본 상품 목록에서 상품이 존재하는지 확인
    boolean existsRecentView(@Param("memberId") Long memberId, @Param("productId") Long productId);
}