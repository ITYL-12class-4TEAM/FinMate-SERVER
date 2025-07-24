package org.scoula.wmti.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.wmti.dto.survey.SurveyResultDTO;
import org.scoula.wmti.entity.SurveyResult;

@Mapper
public interface SurveyResultMapper {

    // CREATE: 설문 결과 저장 (최초1회)
    void saveSurveyResult(SurveyResult surveyResult);

    // 사용자 ID로 설문 결과 조회 (SurveyResult 엔티티 반환)
    SurveyResult findByMemberId(Long memberId);

    // UPDATE: 기존 설문결과 존재시 덮어쓰기 (기존결과는 이력으로 넘김)
    int updateSurveyResult(SurveyResult surveyResult);
}
