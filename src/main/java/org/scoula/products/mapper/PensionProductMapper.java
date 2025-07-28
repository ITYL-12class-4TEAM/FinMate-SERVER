package org.scoula.products.mapper;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.products.dto.response.pension.PensionOptionDTO;
import org.scoula.products.dto.response.pension.PensionProductDTO;

import java.util.List;
import java.util.Map;

@Mapper
public interface PensionProductMapper {

    /**
     * 연금 상품 목록 조회
     */
    List<PensionProductDTO> findPensionProducts(
            @Param("searchText") String searchText,
            @Param("joinWay") String joinWay,
            @Param("minGuarRate") Double minGuarRate
    );

    /**
     * 연금 상품 상세 정보 조회
     */
    PensionProductDTO findByProductId(@Param("productId") Long productId);

    /**
     * 연금 상품 옵션 정보 조회
     */
    List<PensionOptionDTO> findOptionsByProductId(@Param("productId") Long productId);

    /**
     * 연금 상품 유형 목록 조회
     */
    @MapKey("pension_type")
    List<Map<String, String>> getDistinctPensionTypes(@Param("categoryId") Long categoryId);

    /**
     * 보장 수익률 목록 조회
     */
    List<Double> getDistinctGuaranteeRates(@Param("categoryId") Long categoryId);

    /**
     * 납입 기간 목록 조회
     */
    List<Integer> getDistinctPaymentPeriods(@Param("categoryId") Long categoryId);

    /**
     * 최소 월 납입금액 조회
     */
    Integer getMinMonthlyPayment(@Param("categoryId") Long categoryId);

    /**
     * 최대 월 납입금액 조회
     */
    Integer getMaxMonthlyPayment(@Param("categoryId") Long categoryId);

    /**
     * 옵션 ID로 연금 상품 옵션 조회
     *
     * @param optionId 옵션 ID
     * @return 연금 상품 옵션 정보
     */
    PensionOptionDTO findOptionById(@Param("optionId") Long optionId);
}