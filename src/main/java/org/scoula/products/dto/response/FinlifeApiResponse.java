package org.scoula.products.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 금융감독원 금융상품 API 응답 Wrapper DTO
 * 모든 API 응답의 공통 구조를 정의
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinlifeApiResponse<T> {

    // API 응답 상태 정보
    @JsonProperty("result")
    private ApiResult result;

    // 응답 시각 정보
    @JsonProperty("now_dt")
    private String nowDt;

    // 응답 상품 목록 정보
    @JsonProperty("prdt_list")
    private List<T> baseList;

    // 응답 옵션 목록 정보 (금리 정보 등)
    @JsonProperty("option_list")
    private List<Map<String, Object>> optionList;

    /**
     * API 응답 상태 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiResult {
        // 응답 코드
        @JsonProperty("code")
        private String code;

        // 응답 메시지
        @JsonProperty("message")
        private String message;

        // 응답 성공 여부 확인
        public boolean isSuccess() {
            return "0".equals(code) || "00".equals(code);
        }
    }
}