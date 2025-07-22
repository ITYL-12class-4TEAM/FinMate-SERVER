package org.scoula.wmti.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.wmti.entity.SurveyResult;

@Mapper
public interface SurveyResultMapper {
    int insertSurveyResult(SurveyResult surveyResult);
}
