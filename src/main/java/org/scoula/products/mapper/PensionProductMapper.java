package org.scoula.products.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.products.dto.response.pension.PensionOptionDTO;
import org.scoula.products.dto.response.pension.PensionProductDTO;

import java.util.List;

@Mapper
/**
 * 연금 상품 관련 매퍼 인터페이스
 */
public interface PensionProductMapper {

    /**
     * 상품 ID로 연금 상품 상세 정보 조회
     * @param productId 상품 ID(fin_prdt_cd)
     * @return 연금 상품 상세 정보
     */
    PensionProductDTO findByProductId(String productId);

    /**
     * 상품 ID로 연금 상품 옵션 정보 조회
     * @param productId 상품 ID(fin_prdt_cd)
     * @return 연금 상품 옵션 목록
     */
    List<PensionOptionDTO> findOptionsByProductId(String productId);

    /**
     * 연금 상품 목록 조회 (필터링 적용)
     * @param pnsnKind 연금 종류
     * @param prdtType 상품 유형
     * @param guarRate 최소 보장 이율
     * @return 연금 상품 목록
     */
    List<PensionProductDTO> findPensionProducts(
            @Param("pnsnKind") String pnsnKind,
            @Param("prdtType") String prdtType,
            @Param("guarRate") Double guarRate);
}