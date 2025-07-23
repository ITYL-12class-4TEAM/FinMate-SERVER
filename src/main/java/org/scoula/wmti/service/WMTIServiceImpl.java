package org.scoula.wmti.service;

import lombok.RequiredArgsConstructor;
import org.scoula.wmti.domain.WMTICalculator;
import org.scoula.wmti.dto.survey.SurveyResultDTO;
import org.scoula.wmti.entity.SurveyResult;
import org.scoula.wmti.mapper.SurveyResultMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WMTIServiceImpl implements WMTIService {

    private final SurveyResultMapper surveyResultMapper;
    private final WMTICalculator wmtiCalculator;

    @Override
    public String calculateWMTICode(List<Integer> answers) {
        // WMTI 코드 계산
        return wmtiCalculator.calculateWMTICode(answers);
    }

    @Override
    public SurveyResult saveSurveyResult(Long memberId, List<Integer> answers) {
        // WMTI 코드 계산
        String wmtiCode = calculateWMTICode(answers);

        // 설문 결과 DTO 객체 생성
        SurveyResultDTO surveyResultDTO = new SurveyResultDTO();
        surveyResultDTO.setMemberId(memberId);
        surveyResultDTO.setWmtiCode(wmtiCode);
        surveyResultDTO.setAnswersJson(answers.toString());
        surveyResultDTO.setCreatedAt(java.time.LocalDateTime.now());

        // SurveyResultDTO -> SurveyResult (Entity)로 변환
        SurveyResult surveyResult = new SurveyResult();
        surveyResult.setMemberId(surveyResultDTO.getMemberId());
        surveyResult.setWmtiCode(surveyResultDTO.getWmtiCode());
        surveyResult.setAnswersJson(surveyResultDTO.getAnswersJson());
        surveyResult.setCreatedAt(surveyResultDTO.getCreatedAt());

        // 설문결과 DTO를 DB에 저장
        surveyResultMapper.saveSurveyResult(surveyResult);

        return surveyResult;
    }
    @Override
    public SurveyResultDTO getSurveyResultByMemberId(Long memberId) {
        //SurveyResult 엔티티 조회
        SurveyResult surveyResult = surveyResultMapper.findByMemberId(memberId);
        if (surveyResult == null) {
            return null; //설문결과가 없으면 null 반환
        }

        // SurveyResult -> SurveyResultDTO 변환
        SurveyResultDTO surveyResultDTO = new SurveyResultDTO();
        surveyResultDTO.setWmtiCode(surveyResult.getWmtiCode());
        surveyResultDTO.setAnswersJson(surveyResult.getAnswersJson());
        surveyResultDTO.setCreatedAt(surveyResult.getCreatedAt());

        return surveyResultDTO;
    }
}
