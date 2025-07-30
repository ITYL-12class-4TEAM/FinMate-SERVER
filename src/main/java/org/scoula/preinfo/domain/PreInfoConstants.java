package org.scoula.preinfo.domain;

public class PreInfoConstants {

    // ===== [1] 잉여자산/소득 기준 =====
    public static final long HIGH_INCOME_THRESHOLD = 3_000_000L;
    public static final long LOW_SURPLUS_THRESHOLD = 300_000L;
    public static final long SURPLUS_GOOD_20S = 300_000L;
    public static final long SURPLUS_NORMAL_20S = 200_000L;

    public static final long SURPLUS_GOOD_30S_SINGLE = 500_000L;
    public static final long SURPLUS_NORMAL_30S_SINGLE = 300_000L;
    public static final long SURPLUS_GOOD_30S_MARRIED = 700_000L;
    public static final long SURPLUS_NORMAL_30S_MARRIED = 400_000L;

    public static final long SURPLUS_GOOD_40S = 800_000L;
    public static final long SURPLUS_NORMAL_40S = 500_000L;

    // ===== [2] 저축률 기준 =====
    public static final int SAVINGS_GOOD_20S = 25;
    public static final int SAVINGS_NORMAL_20S = 15;

    public static final int SAVINGS_GOOD_30S_MARRIED = 35;
    public static final int SAVINGS_NORMAL_30S_MARRIED = 25;
    public static final int SAVINGS_GOOD_30S_SINGLE = 30;
    public static final int SAVINGS_NORMAL_30S_SINGLE = 20;

    public static final int SAVINGS_GOOD_40S = 40;
    public static final int SAVINGS_NORMAL_40S = 30;

    public static final int SAVINGS_RISK_HIGH = 60;
    public static final int SAVINGS_RISK_MID = 50;
    public static final int SAVINGS_RISK_LOW = 20;
    public static final int SAVINGS_RISK_VERY_LOW = 10;

    // ===== [3] 위험 성향 기준 =====
    public static final long SURPLUS_RISK_VERY_HIGH = 1_500_000L;
    public static final long SURPLUS_RISK_HIGH = 1_000_000L;
    public static final int AGE_YOUNG = 30;
    public static final int AGE_MID = 35;
    public static final int AGE_SENIOR = 45;

    // ===== [4] 재무건전성 점수 구간 =====
    public static final int SCORE_10 = 10;
    public static final int SCORE_30 = 30;
    public static final int SCORE_50 = 50;
    public static final int SCORE_70 = 70;
    public static final int SCORE_85 = 85;
    public static final int SCORE_100 = 100;

    // ===== [5] 추천 투자 비율 =====
    public static final double DEFAULT_RATIO = 0.3;
    public static final double RATIO_CONSERVATIVE = 0.2;
    public static final double RATIO_CAUTION = 0.25;
    public static final double RATIO_ACTIVE = 0.4;
    public static final double RATIO_AGGRESSIVE = 0.5;

    private PreInfoConstants() {} // 인스턴스화 방지
}
