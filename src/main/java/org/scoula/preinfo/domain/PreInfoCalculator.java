package org.scoula.preinfo.domain;

import org.scoula.preinfo.dto.PreInfoRequestDTO;
import org.scoula.preinfo.enums.InvestmentPeriod;
import org.scoula.wmti.enums.RiskPreference;
import org.springframework.stereotype.Component;

@Component
public class PreInfoCalculator {
    /**
     * 투자 여력 분석
     */
    public String calculateInvestmentCapacity(int savingsRate) {
        return savingsRate >= 50 ? "양호" : (savingsRate >= 30 ? "보통" : "부족");
    }

    /**
     * 위험 성향 분류
     */
    public RiskPreference calculateRiskPreference(PreInfoRequestDTO dto, long surplusAmount, int savingsRate) {
        if (savingsRate < 20 || surplusAmount < 300_000L) return RiskPreference.STABILITY;
        if (dto.getAge() > 45 || dto.getPeriod() == InvestmentPeriod.SHORT) return RiskPreference.STABILITY_ORIENTED;
        if (savingsRate >= 60 && dto.getPeriod() == InvestmentPeriod.LONG
                && surplusAmount >= 1_500_000L && dto.getAge() <= 30)
            return RiskPreference.AGGRESSIVE;
        if (savingsRate >= 50 && surplusAmount >= 1_000_000L && dto.getAge() <= 35)
            return RiskPreference.ACTIVELY;
        return RiskPreference.RISK_NEUTRAL;
    }

    /**
     * 결과 유형 분류
     */
    public String calculateResultType(int score) {
        if (score >= 80) return "고수익 지향형";
        else if (score >= 60) return "적극적 설계형";
        else if (score >= 40) return "균형잡힌 실속형";
        else return "소극적 관리형";
    }

}
