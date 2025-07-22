package org.scoula.wmti.mapper;
//WMTI 설문결과를 DB에 저장.
import org.apache.ibatis.annotations.Mapper;
import org.scoula.wmti.entity.SurveyResult;

@Mapper
public interface SurveyResultMapper {
    int insertSurveyResult(SurveyResult surveyResult);
}
