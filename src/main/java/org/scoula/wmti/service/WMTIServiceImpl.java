package org.scoula.wmti.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.scoula.wmti.mapper.SurveyResultMapper;
import org.scoula.wmti.dto.survey.WMTIResultDTO;
import org.scoula.wmti.entity.SurveyResult;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public class WMTIServiceImpl implements WMTIService {
    private final SurveyResultMapper surveyResultMapper;

    @Override
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
    //가중치 설정.
    private double getDelta(int score, double high, double mid) {
        return switch (score) {
            case 1 -> high;
            case 2 -> mid;
            case 3 -> 0.0;
            case 4 -> -mid;
            case 5 -> -high;
            default -> 0.0;
        };
    }

    //설문제출처리 : WMTI계산결과 DB저장 + 응답DTO반환
    public WMTIResultDTO processSurvey(List<Integer> answers, BigInteger memberId) {
        String wmtiCode = calculateWMTICode(answers);

        String answersJson;
        try {
            answersJson = new ObjectMapper().writeValueAsString(answers);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("답안 JSON 변환 실패", e);
        }

        //DB저장용 엔티티 생성
        SurveyResult surveyResult = SurveyResult.builder()
                .memberId(memberId)
                .answers(answers)
                .wmtiCode(wmtiCode)
                .submittedAt(LocalDateTime.now())
                .build();
        //설문결과 저장
        surveyResultMapper.insertSurveyResult(surveyResult);
        //응답 DTO 생성 및 반환
        return WMTIResultDTO.builder()
                .wmtiCode(wmtiCode)
                .submittedAt(surveyResult.getSubmittedAt())
                .build();
    }
}
