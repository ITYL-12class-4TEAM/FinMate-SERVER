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
    private String productType;
    private List<Map<String, Object>> subcategories;
    private List<Map<String, String>> interestRateTypes;
    private List<Integer> saveTerms;  // 가입 기간 또는 납입 기간
    private List<String> joinMethods;
    private List<Map<String, String>> pensionTypes;  // 연금 유형
    private List<Double> guaranteeRates;  // 보장 수익률
    private Map<String, Object> depositAmountOptions;  // 예치 금액 또는 월 납입금 옵션
}