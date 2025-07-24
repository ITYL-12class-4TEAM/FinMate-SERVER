package org.scoula.products.mapper;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.products.dto.response.deposit.DepositOptionDTO;
import org.scoula.products.dto.response.deposit.DepositProductDTO;

import java.util.List;
import java.util.Map;

@Mapper
/**
 * 예금 상품 관련 매퍼 인터페이스
 */
public interface DepositProductMapper {

    /**
     * 상품 ID로 예금 상품 상세 정보 조회
     *
     * @param productId 상품 ID(fin_prdt_cd)
     * @return 예금 상품 상세 정보
     */
    DepositProductDTO findByProductId(Long productId);

    /**
     * 상품 ID로 예금 상품 옵션 정보 조회
     *
     * @param productId 상품 ID(fin_prdt_cd)
     * @return 예금 상품 옵션 목록
     */
    List<DepositOptionDTO> findOptionsByProductId(Long productId);

    /**
     * 예금 상품 목록 조회 (필터링 적용)
     *
     * @param saveTrm      가입 기간
     * @param intrRateType 이자율 유형
     * @param minIntrRate  최소 이자율
     * @return 예금 상품 목록
     */
    List<DepositProductDTO> findDepositProducts(
            @Param("saveTrm") Integer saveTrm,
            @Param("intrRateType") String intrRateType,
            @Param("minIntrRate") Double minIntrRate);


    /**
     * 카테고리별 최소 예치 금액 조회
     */
    Integer getMinDepositAmount(@Param("categoryId") Long categoryId);

    /**
     * 카테고리별 최대 예치 금액 조회
     */
    Integer getMaxDepositAmount(@Param("categoryId") Long categoryId);

    /**
     * 카테고리별 가입 기간 목록 조회
     */
    List<Integer> getDistinctSaveTerms(@Param("categoryId") Long categoryId);

    /**
     * 카테고리별 이자율 유형 목록 조회
     */
    @MapKey("intr_rate_type")
    List<Map<String, String>> getDistinctInterestRateTypes(@Param("categoryId") Long categoryId);
}
