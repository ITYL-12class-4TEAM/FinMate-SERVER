package org.scoula.preinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreInfoResponseDTO {
    private String preInfoId;
    private Long userId;
    private LocalDateTime savedAt;
    private String surveyToken;

    private AnalysisResult analysis;
    private NextStep nextStep;
    private String estimatedTime = "5분"; // 고정 문자열

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnalysisResult {
        private Long disposableIncome;
        private Integer savingsRate;
        private Integer financialHealthScore;
        private String investmentCapacity;
        private Long recommendedMonthlyInvestment;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NextStep {
        private String url;
        private String description;
    }
}

