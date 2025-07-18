package org.scoula.wmti.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.scoula.wmti.entity.SurveyResult;

@Mapper
public interface SurveyResultMapper {
    @Insert("""
        INSERT INTO wmti_profile (member_id, wmti_code, wmti_id, answers_json, submitted_at)
        VALUES (#{userId}, #{wmtiCode}, #{answersJson}, #{submittedAt})
""")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertSurveyResult(SurveyResult surveyResult);

}
