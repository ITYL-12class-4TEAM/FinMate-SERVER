package org.scoula.products.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 금융 상품 비교 결과 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCompareResponse {

    // 상품 유형 (deposit: 예금, saving: 적금, pension: 연금저축)
    private String productType;

    // 비교 대상 상품 목록
    private List<?> products;

    // 비교 결과 데이터
    // 각 항목별 비교 정보를 Map 형태로 저장
    private Map<String, Object> comparisonData;

    // 비교 항목별 설명
    private Map<String, String> itemDescriptions;

    // 선택된 저축 기간 (개월)
    private Integer saveTrm;

    // 비교 요약 정보
    private ComparisonSummary summary;

    /**
     * 비교 요약 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComparisonSummary {
        // 최고 금리 상품 정보
        private ProductSummary highestRateProduct;

        // 가입 방법이 다양한 상품 정보
        private ProductSummary mostJoinWaysProduct;

        // 가입 대상이 넓은 상품 정보
        private ProductSummary widestTargetProduct;

        // 수령 가능 나이가 가장 빠른 상품 정보
        private ProductSummary earliestStartAgeProduct;
    }

    /**
     * 상품 요약 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductSummary {
        // 금융상품 코드
        private String finPrdtCd;

        // 금융회사명
        private String korCoNm;

        // 금융상품명
        private String finPrdtNm;

        // 상품 특성 값 (금리, 가입방법 수 등)
        private Object value;
    }
}