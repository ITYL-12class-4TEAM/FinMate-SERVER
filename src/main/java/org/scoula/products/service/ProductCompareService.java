package org.scoula.products.service;

import org.scoula.products.dto.request.ProductCompareRequest;
import org.scoula.products.dto.response.ProductCompareResponse;

/**
 * 금융 상품 비교 서비스 인터페이스
 */
public interface ProductCompareService {

    /**
     * 금융 상품들을 비교하여 비교 결과를 반환합니다.
     *
     * @param request 비교 요청 정보 (상품 유형, 상품 ID 목록)
     * @return 비교 결과 정보
     */
    ProductCompareResponse compareProducts(ProductCompareRequest request);

    /**
     * 상품 ID 목록을 기반으로 상품들을 비교합니다.
     *
     * @param productType 상품 유형 (deposit, saving, pension)
     * @param productIds 비교할 상품 ID 목록
     * @return 비교 결과 정보
     */
    ProductCompareResponse compareProducts(String productType, java.util.List<String> productIds);
}