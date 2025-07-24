package org.scoula.wmti.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;
import java.util.List;

@Component
public class WMTIAnalysis {
    private final ObjectMapper objectMapper = new ObjectMapper();
    // JSON 파일에서 성향 코드에 맞는 분석 결과와 추천 상품 반환
    public Map<String, Object> getAnalysisByWMTICode(String wmtiCode) {
        Map<String, Map<String, Object>> data = null;

        try {
            // JSON 파일 로드 및 성향 코드에 맞는 분석 내용 처리
            InputStream inputStream = getClass().getResourceAsStream("/wmti-analysis.json");

            // JSON 파일을 Map으로 변환
            data = objectMapper.readValue(inputStream, Map.class);

            // 성향 코드에 맞는 데이터가 있으면 반환
            if (data.containsKey(wmtiCode)) {
                return data.get(wmtiCode);
            } else {
                // 예외 처리 (없는 코드가 들어오면 메시지 반환)
                return Map.of("message", "No analysis found for code: " + wmtiCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //예외처리 후 ResponseCode로 관리
            return Map.of("message", "Error reading analysis data.", "code", "WMTI_SURVEY_PROCESSING_FAILED");
        }
    }
}

