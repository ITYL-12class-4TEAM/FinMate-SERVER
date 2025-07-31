package org.scoula.products.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterOptionsResponse {
    // 호환성을 위해 유지
    private String productType;

    // 카테고리 및 서브카테고리 정보
    private Long categoryId;
    private Long subcategoryId;
    private List<Map<String, Object>> subcategories;

    // 기존 필터 옵션
    private List<Map<String, String>> interestRateTypes;
    private List<Integer> saveTerms;
    private List<String> joinMethods;
    private List<String> banks;

    // 금액 옵션 - 서브카테고리별 다른 의미
    private Map<String, Object> depositAmountOptions;  // 예치 금액 (정기예금용)
    private Map<String, Object> monthlyPaymentOptions;  // 월 납입 금액 (자유적금용)

    // 연금 상품 전용 필터
    private List<Map<String, String>> pensionTypes;
    private List<Double> guaranteeRates;
}