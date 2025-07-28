package org.scoula.wmti.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.scoula.wmti.dto.survey.WMTIProfileDTO;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Map;

@Component
@Log4j2
public class WMTIAnalysis {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Map<String, WMTIProfileDTO> analysisData;

    @PostConstruct
    public void loadAnalysisData() {
        try (InputStream inputStream = getClass().getResourceAsStream("/json/survey/wmti_analysis.json")) {
            analysisData = objectMapper.readValue(inputStream, new TypeReference<>() {});
            log.info("WMTI 분석 데이터 로딩 성공: {}개 항목", analysisData.size());
        } catch (Exception e) {
            log.error("WMTI 분석 JSON 로딩 실패", e);
            throw new IllegalStateException("WMTI 분석 데이터 로딩에 실패했습니다.", e);
        }
    }

    public WMTIProfileDTO getAnalysisByWMTICode(String wmtiCode) {
        if (analysisData == null || !analysisData.containsKey(wmtiCode)) {
            log.warn("WMTI 코드 [{}]에 대한 분석 결과를 찾을 수 없습니다.", wmtiCode);
            return null;
        }
        return analysisData.get(wmtiCode);
    }
}
