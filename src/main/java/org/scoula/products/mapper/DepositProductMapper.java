package org.scoula.products.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.products.dto.response.deposit.DepositOptionDTO;
import org.scoula.products.dto.response.deposit.DepositProductDTO;

import java.util.List;

@Mapper
/**
 * 예금 상품 관련 매퍼 인터페이스
 */
public interface DepositProductMapper {

    /**
     * 상품 ID로 예금 상품 상세 정보 조회
     * @param productId 상품 ID(fin_prdt_cd)
     * @return 예금 상품 상세 정보
     */
    DepositProductDTO findByProductId(String productId);

    /**
     * 상품 ID로 예금 상품 옵션 정보 조회
     * @param productId 상품 ID(fin_prdt_cd)
     * @return 예금 상품 옵션 목록
     */
    List<DepositOptionDTO> findOptionsByProductId(String productId);

    /**
     * 예금 상품 목록 조회 (필터링 적용)
     * @param saveTrm 가입 기간
     * @param intrRateType 이자율 유형
     * @param minIntrRate 최소 이자율
     * @return 예금 상품 목록
     */
    List<DepositProductDTO> findDepositProducts(
            @Param("saveTrm") Integer saveTrm,
            @Param("intrRateType") String intrRateType,
            @Param("minIntrRate") Double minIntrRate);
}