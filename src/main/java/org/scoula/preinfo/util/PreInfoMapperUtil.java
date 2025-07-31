package org.scoula.preinfo.util;

import org.scoula.preinfo.dto.PreInfoRequestDTO;
import org.scoula.preinfo.dto.PreInfoResponseDTO;
import org.scoula.preinfo.entity.PreInformation;
import org.scoula.preinfo.enums.InvestmentCapacity;
import org.scoula.preinfo.enums.InvestmentType;
import org.scoula.wmti.enums.RiskPreference;

import java.time.LocalDateTime;

public class PreInfoMapperUtil {

    public static PreInformation toEntity(String preInfoId, Long userId, PreInfoRequestDTO dto,
                                          long surplus, int savingsRate, int score,
                                          long recommendedMonthlyInvestment,
                                          InvestmentCapacity investmentCapacity,
                                          RiskPreference riskPreference,
                                          InvestmentType resultType,
                                          LocalDateTime now) {

        return PreInformation.builder()
                .preInfoId(preInfoId)
                .memberId(userId)
                .username(dto.getUsername())
                .age(dto.getAge())
                .married(dto.getMarried())
                .monthlyIncome(dto.getMonthlyIncome())
                .fixedCost(dto.getFixedCost())
                .surplusAmount(surplus)
                .period(dto.getPeriod())
                .purposeCategory(dto.getPurposeCategory())
                .savingsRate(savingsRate)
                .financialHealthScore(score)
                .investmentCapacity(investmentCapacity)
                .recommendedMonthlyInvestment(recommendedMonthlyInvestment)
                .resultType(resultType)
                .riskPreference(riskPreference)
                .platform(dto.getPlatform())
                .userAgent(dto.getUserAgent())
                .screenSize(dto.getScreenSize())
                .createdAt(now)
                .build();
    }

    public static PreInfoResponseDTO toResponseDTO(String preInfoId, Long userId, String token,
                                                   LocalDateTime now,
                                                   long surplus,
                                                   int savingsRate,
                                                   int score,
                                                   long recommendedMonthlyInvestment,
                                                   InvestmentCapacity investmentCapacity,
                                                   InvestmentType resultType,
                                                   RiskPreference riskPreference) {
        return PreInfoResponseDTO.builder()
                .preInfoId(preInfoId)
                .userId(userId)
                .savedAt(now)
                .surveyToken(token)
                .analysis(PreInfoResponseDTO.AnalysisResult.builder()
                        .disposableIncome(surplus)
                        .savingsRate(savingsRate)
                        .financialHealthScore(score)
                        .investmentCapacity(investmentCapacity)
                        .recommendedMonthlyInvestment(recommendedMonthlyInvestment)
                        .resultType(resultType)
                        .riskPreference(riskPreference)
                        .build())
                .nextStep(PreInfoResponseDTO.NextStep.builder()
                        .url("/survey/questionnaire")
                        .description("이제 투자 성향 검사를 진행해주세요.")
                        .build())
                .estimatedTime("15분")
                .build();
    }

    private PreInfoMapperUtil() {}
}

