package org.scoula.wmti.domain;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WMTICalculator {

    /**
     * 설문 응답을 바탕으로 WMTI 코드를 계산하는 메서드
     *
     * @param answers 설문 응답
     * @return 계산된 WMTI 코드
     */
    public String calculateWMTICode(List<Integer> answers) {
        double aScore = 65.0;
        double pScore = 60.0;
        double mScore = 60.0;
        double lScore = 60.0;

        for (int i = 0; i < answers.size(); i++) {
            int qNum = i + 1;
            int score = answers.get(i);

            switch (qNum) {
                case 1:
                    // A vs I
                    if (score == 1) aScore = 65.0;
                    else if (score == 2) aScore = 60.0;
                    else if (score == 3) aScore = 50.0;
                    else if (score == 4) aScore = 45.0;
                    else if (score == 5) aScore = 40.0;
                    break;
                case 2: case 4: case 10: case 11: case 15: case 17: case 20:
                    // P vs B
                    pScore += getDelta(score, 6.0, 3.0);
                    break;
                case 3: case 5: case 7: case 8: case 13: case 14: case 16: case 18:
                    // M vs W
                    mScore += getDelta(score, 5.0, 2.5);
                    break;
                case 6: case 9: case 12: case 19:
                    // L vs C
                    lScore += getDelta(score, 10.0, 5.0);
                    break;
            }
        }

        // 성향 결정
        StringBuilder code = new StringBuilder();
        code.append(aScore >= 50 ? "A" : "I");
        code.append(pScore >= 50 ? "P" : "B");
        code.append(mScore >= 50 ? "M" : "W");
        code.append(lScore >= 50 ? "L" : "C");

        return code.toString();
    }

    // 가중치 설정
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
}
