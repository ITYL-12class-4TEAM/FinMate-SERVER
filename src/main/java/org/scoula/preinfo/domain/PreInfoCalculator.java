package org.scoula.preinfo.domain;

import org.scoula.preinfo.dto.PreInfoRequestDTO;
import org.scoula.preinfo.enums.InvestmentPeriod;
import org.scoula.wmti.enums.RiskPreference;
import org.springframework.stereotype.Component;

import org.scoula.preinfo.enums.InvestmentCapacity;
import org.scoula.preinfo.enums.InvestmentPeriod;
import org.scoula.preinfo.enums.InvestmentType;
import org.scoula.preinfo.domain.PreInfoConstants;

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
        if (income <= 0) return 0;
        return (int) ((double) surplus / income * 100);
    }
    /**
     * 재무건전성 점수 계산 (금융감독원, 서민금융진흥원 등에서는 ‘저축률 20~40%’를 바람직한 재무상태 기준으로 제시)
     */
    public int calculateFinancialHealthScore(int savingsRate) {
        if (savingsRate < 10) return PreInfoConstants.SCORE_10;
        if (savingsRate < 20) return PreInfoConstants.SCORE_30;
        if (savingsRate < 30) return PreInfoConstants.SCORE_50;
        if (savingsRate < 40) return PreInfoConstants.SCORE_70;
        if (savingsRate < 60) return PreInfoConstants.SCORE_85;
        return PreInfoConstants.SCORE_100;
    }

    /**
     * 투자 여력 분석
     * (금융감독원 '금융생활설계 가이드북' : 생애 주기에 따라 월급의 30~50% 저축 권고 // )
     * (금융감독원 '청년 금융백서(2023)' : 2030 세대 평균 월소득: 약 230만 원, 20~30대 초반은 30% 저축률, 50만~70만 원 잉여자산이 현실적인 평균선)
     * (통계청 사회조사: 혼인 상태에 따른 지출 수준 및 가구구조 분포)
     * (JP Morgan Guide to Retirement : 가처분소득 대비 저축률 20% 이상이 장기적 투자에 적합)
     */
    public InvestmentCapacity calculateInvestmentCapacity(int savingsRate, long surplusAmount, long monthlyIncome, int age,boolean married) {
        //예외적 위험 상황 : 고소득 + 저축률/잉여자산이 낮음 => 수입 대비 소비가 과도한 상태, 즉 생활 수준에 비해 저축습관이 매우 부족한 고위험 지출 패턴.
        if (monthlyIncome >= PreInfoConstants.HIGH_INCOME_THRESHOLD && savingsRate < PreInfoConstants.SAVINGS_RISK_VERY_LOW && surplusAmount < PreInfoConstants.LOW_SURPLUS_THRESHOLD) {
            return InvestmentCapacity.RISK;  //위험
        }

        if (age < 30) { // 20대 후반까지는 결혼여부보다 전체 평균에 집중
            if (savingsRate >= PreInfoConstants.SAVINGS_GOOD_20S && surplusAmount >= PreInfoConstants.SURPLUS_GOOD_20S) return InvestmentCapacity.GOOD;
            if (savingsRate >= PreInfoConstants.SAVINGS_NORMAL_20S && surplusAmount >= PreInfoConstants.SURPLUS_NORMAL_20S) return InvestmentCapacity.NORMAL;
            return InvestmentCapacity.INSUFFICIENT;  //부족

        } else if (age < 40) { // 30대 → 기혼 여부가 의미 있게 반영됨
            if (married) {
                if (savingsRate >= PreInfoConstants.SAVINGS_GOOD_30S_MARRIED && surplusAmount >= PreInfoConstants.SURPLUS_GOOD_30S_MARRIED) return InvestmentCapacity.GOOD;
                if (savingsRate >= PreInfoConstants.SAVINGS_NORMAL_30S_MARRIED && surplusAmount >= PreInfoConstants.SURPLUS_NORMAL_30S_MARRIED) return InvestmentCapacity.NORMAL;
            } else {
                if (savingsRate >= PreInfoConstants.SAVINGS_GOOD_30S_SINGLE && surplusAmount >= PreInfoConstants.SURPLUS_GOOD_30S_SINGLE) return InvestmentCapacity.GOOD;
                if (savingsRate >= PreInfoConstants.SAVINGS_NORMAL_30S_SINGLE && surplusAmount >= PreInfoConstants.SURPLUS_NORMAL_30S_SINGLE) return InvestmentCapacity.NORMAL;
            }
            return InvestmentCapacity.INSUFFICIENT;   //부족


        } else { // 40대 이상 → 결혼 여부와 관계없이 자산안정성이 중요
            if (savingsRate >= PreInfoConstants.SAVINGS_GOOD_40S && surplusAmount >= PreInfoConstants.SURPLUS_GOOD_40S) return InvestmentCapacity.GOOD;
            if (savingsRate >= PreInfoConstants.SAVINGS_NORMAL_40S && surplusAmount >= PreInfoConstants.SURPLUS_NORMAL_40S) return InvestmentCapacity.NORMAL;
            return InvestmentCapacity.INSUFFICIENT;  //부족
        }
    }

    /**
     * 위험 성향 분류
     */
    public RiskPreference calculateRiskPreference(PreInfoRequestDTO dto, long surplusAmount, int savingsRate) {
        if (savingsRate < PreInfoConstants.SAVINGS_RISK_LOW || surplusAmount < PreInfoConstants.LOW_SURPLUS_THRESHOLD) return RiskPreference.STABILITY;
        if (dto.getAge() > PreInfoConstants.AGE_SENIOR || dto.getPeriod() == InvestmentPeriod.SHORT) return RiskPreference.STABILITY_ORIENTED;
        if (savingsRate >= PreInfoConstants.SAVINGS_RISK_HIGH && dto.getPeriod() == InvestmentPeriod.LONG
                && surplusAmount >= PreInfoConstants.SURPLUS_RISK_VERY_HIGH && dto.getAge() <= PreInfoConstants.AGE_YOUNG)
            return RiskPreference.AGGRESSIVE;
        if (savingsRate >= PreInfoConstants.SAVINGS_RISK_MID && surplusAmount >= PreInfoConstants.SURPLUS_RISK_HIGH && dto.getAge() <= PreInfoConstants.AGE_MID)
            return RiskPreference.ACTIVELY;
        return RiskPreference.RISK_NEUTRAL;
    }

    /**
     * 결과 유형 분류
     */
    public InvestmentType calculateResultType(int score) {
        if (score >= 80) return InvestmentType.AGGRESSIVE;
        else if (score >= 60) return InvestmentType.ACTIVE;
        else if (score >= 40) return InvestmentType.BALANCED;
        else return InvestmentType.PASSIVE;
    }
    /**
     * 추천 월 투자액 계산
     */
    public long calculateRecommendedMonthlyInvestment(InvestmentPeriod period, RiskPreference riskPreference, long surplusAmount) {
        double ratio = PreInfoConstants.DEFAULT_RATIO;

        if (period == InvestmentPeriod.LONG && riskPreference == RiskPreference.AGGRESSIVE) {
            ratio = PreInfoConstants.RATIO_AGGRESSIVE;
        } else if (period == InvestmentPeriod.LONG && riskPreference == RiskPreference.ACTIVELY) {
            ratio = PreInfoConstants.RATIO_ACTIVE;
        } else if (riskPreference == RiskPreference.STABILITY) {
            ratio = PreInfoConstants.RATIO_CONSERVATIVE;
        } else if (riskPreference == RiskPreference.STABILITY_ORIENTED) {
            ratio = PreInfoConstants.RATIO_CAUTION;
        }
        return (long) (surplusAmount * ratio);
    }

}
