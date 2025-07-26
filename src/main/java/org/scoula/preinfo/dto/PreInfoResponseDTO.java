package org.scoula.preinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.wmti.enums.RiskPreference;

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
    private String estimatedTime = "15분"; // 고정 문자열

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
        private String resultType;  //사전정보입력 결과 도출된 투자자유형
        private RiskPreference riskPreference; //사전정보입력 결과 도출된 위험 성향 ENUM(’안정형’, ‘안정추구형’, ‘위험중립형’, ‘적극투자형’, ‘공격투자형’
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NextStep {
        //사전정보 입력이 끝나면 wmti설문조사 페이지url로 자동연결
        private String url;         //WMTI설문조사 페이지로 연결
        private String description; //
    }
}

