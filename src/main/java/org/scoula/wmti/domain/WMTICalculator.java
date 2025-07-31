package org.scoula.wmti.domain;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WMTICalculator {
    //상수추출
    private static final double DEFAULT_A_SCORE = 65.0;
    private static final double DEFAULT_P_SCORE = 60.0;
    private static final double DEFAULT_M_SCORE = 60.0;
    private static final double DEFAULT_L_SCORE = 60.0;

    private static final double DELTA_HIGH_P = 6.0;
    private static final double DELTA_MID_P = 3.0;

    private static final double DELTA_HIGH_M = 5.0;
    private static final double DELTA_MID_M = 2.5;

    private static final double DELTA_HIGH_L = 10.0;
    private static final double DELTA_MID_L = 5.0;

    /**
     * WMTI 코드만 반환하는 메서드 (calculateScores 재활용)
     */
    public String calculateWMTICode(List<Integer> answers) {
        WMTIScoreResult scores = calculateScores(answers);

        StringBuilder code = new StringBuilder();
        code.append(scores.getAScore() >= 50 ? "A" : "I");
        code.append(scores.getPScore() >= 50 ? "P" : "B");
        code.append(scores.getMScore() >= 50 ? "M" : "W");
        code.append(scores.getLScore() >= 50 ? "L" : "C");

        return code.toString();
    }

    /**
     * A/P/M/L 점수만 계산하는 메서드 (공통 로직 분리)
     */
    public WMTIScoreResult calculateScores(List<Integer> answers) {
        double aScore = DEFAULT_A_SCORE;
        double pScore = DEFAULT_P_SCORE;
        double mScore = DEFAULT_M_SCORE;
        double lScore = DEFAULT_L_SCORE;

        for (int i = 0; i < answers.size(); i++) {
            int qNum = i + 1;
            int score = answers.get(i);

            if (qNum == 1) {
                aScore = convertAScore(score);
            } else if (isPQuestion(qNum)) {
                pScore += getDelta(score, DELTA_HIGH_P, DELTA_MID_P);
            } else if (isMQuestion(qNum)) {
                mScore += getDelta(score, DELTA_HIGH_M, DELTA_MID_M);
            } else if (isLQuestion(qNum)) {
                lScore += getDelta(score, DELTA_HIGH_L, DELTA_MID_L);
            }
        }

        // 클램핑
        aScore = clampScore(aScore);
        pScore = clampScore(pScore);
        mScore = clampScore(mScore);
        lScore = clampScore(lScore);

        // 반대 성향은 100 - X
        double iScore = 100 - aScore;
        double bScore = 100 - pScore;
        double wScore = 100 - mScore;
        double cScore = 100 - lScore;

        return new WMTIScoreResult(aScore, iScore, pScore, bScore, mScore, wScore, lScore, cScore);
    }

    /**
     * A형 성향 점수 변환 (1번 문항만 해당)
     */
    private double convertAScore(int score) {
        switch (score) {
            case 1: return 65.0;
            case 2: return 60.0;
            case 3: return 50.0;
            case 4: return 45.0;
            case 5: return 40.0;
            default: return 50.0;
        }
    }

    /**
     * 점수 변화 계산 (공통)
     */
    private double getDelta(int score, double high, double mid) {
        switch (score) {
            case 1: return high;
            case 2: return mid;
            case 3: return 0.0;
            case 4: return -mid;
            case 5: return -high;
            default: return 0.0;
        }
    }
    private double clampScore(double score) {
        return Math.max(0, Math.min(100, score));
    }

    // 각 성향 문항 판단
    private boolean isPQuestion(int q) {
        return q == 2 || q == 4 || q == 10 || q == 11 || q == 15 || q == 17 || q == 20;
    }

    private boolean isMQuestion(int q) {
        return q == 3 || q == 5 || q == 7 || q == 8 || q == 13 || q == 14 || q == 16 || q == 18;
    }

    private boolean isLQuestion(int q) {
        return q == 6 || q == 9 || q == 12 || q == 19;
    }
}
