package org.scoula.preinfo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.scoula.wmti.enums.RiskPreference;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "사전 정보 조회 결과 DTO")
public class PreInfoViewDTO {

    @ApiModelProperty(value = "사전정보 고유 ID", example = "PRE_7_20250726")
    private String preInfoId;

    @ApiModelProperty(value = "회원 고유 ID", example = "7")
    private Long memberId;

    @ApiModelProperty(value = "사용자 이름", example = "홍길동")
    private String username;

    @ApiModelProperty(value = "나이", example = "28")
    private Integer age;

    @ApiModelProperty(value = "기혼여부", example = "true")
    private Boolean married;

    @ApiModelProperty(value = "월 소득", example = "3000000")
    private Long monthlyIncome;

    @ApiModelProperty(value = "월 고정 지출", example = "1200000")
    private Long fixedCost;

    @ApiModelProperty(value = "월 잉여 자산", example = "1800000")
    private Long surplusAmount;

    @ApiModelProperty(value = "저축률 (%)", example = "60")
    private Integer savingsRate;

    @ApiModelProperty(value = "재무건전성 점수", example = "80")
    private Integer financialHealthScore;

    @ApiModelProperty(value = "투자 여력", example = "보통")
    private String investmentCapacity;

    @ApiModelProperty(value = "추천 월 투자금액", example = "500000")
    private Long recommendedMonthlyInvestment;

    @ApiModelProperty(value = "도출된 투자자 유형", example = "소극적 실용가형")
    private String resultType;

    @ApiModelProperty(value = "위험 성향", example = "위험중립형")
    private RiskPreference riskPreference;

    @ApiModelProperty(value = "입력 시각", example = "2025-07-26T10:45:00")
    private LocalDateTime createdAt;

    public static PreInfoViewDTO from(org.scoula.preinfo.entity.PreInformation e) {
        return PreInfoViewDTO.builder()
                .preInfoId(e.getPreInfoId())
                .memberId(e.getMemberId())
                .username(e.getUsername())
                .age(e.getAge())
                .married(e.getMarried())
                .monthlyIncome(e.getMonthlyIncome())
                .fixedCost(e.getFixedCost())
                .surplusAmount(e.getSurplusAmount())
                .savingsRate(e.getSavingsRate())
                .financialHealthScore(e.getFinancialHealthScore())
                .investmentCapacity(e.getInvestmentCapacity())
                .recommendedMonthlyInvestment(e.getRecommendedMonthlyInvestment())
                .resultType(e.getResultType())
                .riskPreference(e.getRiskPreference())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
