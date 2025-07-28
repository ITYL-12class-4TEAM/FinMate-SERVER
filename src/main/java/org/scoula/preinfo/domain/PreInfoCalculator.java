package org.scoula.preinfo.domain;

import org.scoula.preinfo.dto.PreInfoRequestDTO;
import org.scoula.preinfo.enums.InvestmentPeriod;
import org.scoula.wmti.enums.RiskPreference;
import org.springframework.stereotype.Component;

@Component
public class PreInfoCalculator {
    /**
     * 잉여자산 계산
     */
    public long calculateSurplusAmount(long income, long fixedCost) {
        return income - fixedCost;
    }
    /**
     * 저축률 계산 (%)
     */
    public int calculateSavingsRate(long income, long surplus) {
        if (income == 0) return 0;
        return (int) ((double) surplus / income * 100);
    }
    /**
     * 재무건전성 점수 계산 (금융감독원, 서민금융진흥원 등에서는 ‘저축률 20~40%’를 바람직한 재무상태 기준으로 제시)
     */
    public int calculateFinancialHealthScore(int savingsRate) {
        if (savingsRate < 10) return 10;
        if (savingsRate < 20) return 30;
        if (savingsRate < 30) return 50;
        if (savingsRate < 40) return 70;
        if (savingsRate < 60) return 85;
        return 100;
    }

    /**
     * 투자 여력 분석
     * (금융감독원 '금융생활설계 가이드북' : 생애 주기에 따라 월급의 30~50% 저축 권고 // )
     * (금융감독원 '청년 금융백서(2023)' : 2030 세대 평균 월소득: 약 230만 원, 20~30대 초반은 30% 저축률, 50만~70만 원 잉여자산이 현실적인 평균선)
     * (통계청 사회조사: 혼인 상태에 따른 지출 수준 및 가구구조 분포)
     * (JP Morgan Guide to Retirement : 가처분소득 대비 저축률 20% 이상이 장기적 투자에 적합)
     */
    public String calculateInvestmentCapacity(int savingsRate, long surplusAmount, long monthlyIncome, int age,boolean married) {
        //예외적 위험 상황 : 고소득 + 저축률/잉여자산이 낮음 => 수입 대비 소비가 과도한 상태, 즉 생활 수준에 비해 저축습관이 매우 부족한 고위험 지출 패턴.
        if (monthlyIncome >= 3_000_000L && savingsRate < 10 && surplusAmount < 300_000L) {
            return "위험";
        }

        if (age < 30) { // 20대 후반까지는 결혼여부보다 전체 평균에 집중
            if (savingsRate >= 25 && surplusAmount >= 300_000L) return "양호";
            if (savingsRate >= 15 && surplusAmount >= 200_000L) return "보통";
            return "부족";

        } else if (age < 40) { // 30대 → 기혼 여부가 의미 있게 반영됨
            if (married) {
                if (savingsRate >= 35 && surplusAmount >= 700_000L) return "양호";
                if (savingsRate >= 25 && surplusAmount >= 400_000L) return "보통";
            } else {
                if (savingsRate >= 30 && surplusAmount >= 500_000L) return "양호";
                if (savingsRate >= 20 && surplusAmount >= 300_000L) return "보통";
            }
            return "부족";

        } else { // 40대 이상 → 결혼 여부와 관계없이 자산안정성이 중요
            if (savingsRate >= 40 && surplusAmount >= 800_000L) return "양호";
            if (savingsRate >= 30 && surplusAmount >= 500_000L) return "보통";
            return "부족";
        }
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
    /**
     * 추천 월 투자액 계산
     */
    public long calculateRecommendedMonthlyInvestment(InvestmentPeriod period, RiskPreference riskPreference, long surplusAmount) {
        double ratio = 0.3; // 기본값

        if (period == InvestmentPeriod.LONG && riskPreference == RiskPreference.AGGRESSIVE) {
            ratio = 0.5;
        } else if (period == InvestmentPeriod.LONG && riskPreference == RiskPreference.ACTIVELY) {
            ratio = 0.4;
        } else if (riskPreference == RiskPreference.STABILITY) {
            ratio = 0.2;
        } else if (riskPreference == RiskPreference.STABILITY_ORIENTED) {
            ratio = 0.25;
        }
        return (long) (surplusAmount * ratio);
    }

}
