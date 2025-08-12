package org.scoula.chatgpt.dto.portfolio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfoResponse {
    private boolean success;
    private String category;
    private String subcategory;

    // 다중 옵션들
    private List<TermOption> termOptions;     // 기간별 옵션들
    private List<RateOption> rateOptions;     // 금리 옵션들

    // 추천/기본값들
    private Integer recommendedTerm;          // 추천 기간 (개월)
    private Double baseRate;                  // 기본 금리
    private Double maxRate;                   // 최고 금리

    // 금액 정보 (참고용)
    private Long minAmount;                   // 최소 금액
    private Long maxAmount;                   // 최대 금액
    private String amountType;                // "MONTHLY" | "TOTAL" | "PREMIUM"

    // 기타 정보
    private String memo;
    private Double confidence;
    private String rawResponse;

    // 포트폴리오 생성용 추가 필드
    private String customProductName;
    private String customCompanyName;
    private String riskLevel;                 // "LOW" | "MEDIUM" | "HIGH"
}