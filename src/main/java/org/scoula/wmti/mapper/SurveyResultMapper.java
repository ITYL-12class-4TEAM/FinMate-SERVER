package org.scoula.wmti.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.wmti.dto.survey.SurveyResultDTO;
import org.scoula.wmti.entity.SurveyResult;

@Mapper
public interface SurveyResultMapper {

    // 설문 결과 저장
    void saveSurveyResult(SurveyResult surveyResult);

    // 사용자 ID로 설문 결과 조회 (SurveyResult 엔티티 반환)
    SurveyResult findByMemberId(Long memberId);
}
