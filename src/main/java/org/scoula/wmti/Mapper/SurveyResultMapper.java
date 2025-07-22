package org.scoula.wmti.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.scoula.wmti.entity.SurveyResult;

@Mapper
public interface SurveyResultMapper {
    int insertSurveyResult(SurveyResult surveyResult);
}
