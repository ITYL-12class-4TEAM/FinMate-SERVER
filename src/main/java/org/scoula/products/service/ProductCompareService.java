package org.scoula.products.service;

import org.scoula.products.dto.request.ProductCompareRequest;
import org.scoula.products.dto.response.ProductCompareResponse;

import java.util.List;
import java.util.Map;

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
     * @param productType 상품 유형 (deposit, pension)
     * @param productIds  비교할 상품 ID 목록
     * @return 비교 결과 정보
     */
    ProductCompareResponse compareProducts(String productType, List<String> productIds);

    /**
     * 예금 상품 비교
     *
     * @param productIds 상품 ID 목록
     * @return 비교 결과 정보
     */
    ProductCompareResponse compareDepositProducts(List<String> productIds);

    /**
     * 연금 상품 비교 (문자열 ID 목록)
     *
     * @param productIds 상품 ID 목록 (문자열)
     * @return 비교 결과 정보
     */
    ProductCompareResponse comparePensionProducts(List<String> productIds, String optionId);

    /**
     * 연금 상품 비교 (Long 타입 ID 목록)
     *
     * @param productIds 상품 ID 목록 (Long)
     * @return 비교 결과 정보
     */
    ProductCompareResponse comparePensionProductsLong(List<Long> productIds);

    // 새 메서드 추가
    ProductCompareResponse compareProductsWithOptions(
            String productType, Map<String, Map<String, String>> productOptions);
}