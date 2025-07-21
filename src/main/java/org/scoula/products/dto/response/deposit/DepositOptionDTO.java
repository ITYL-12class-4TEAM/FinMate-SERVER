package org.scoula.products.dto.response.deposit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 정기예금 상품 옵션(금리) 정보 DTO
 * 금융감독원 금융상품 한눈에 API 기준으로 구성
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositOptionDTO {

    // 금융상품 코드
    @JsonProperty("fin_prdt_cd")
    private String finPrdtCd;

    // 저축 기간 (단위: 개월)
    @JsonProperty("save_trm")
    private Integer saveTrm;

    // 저축 금리 유형 (단리/복리)
    @JsonProperty("intr_rate_type")
    private String intrRateType;

    // 저축 금리 유형명
    @JsonProperty("intr_rate_type_nm")
    private String intrRateTypeNm;

    // 저축 기본 금리
    @JsonProperty("intr_rate")
    private Double intrRate;

    // 저축 최고 우대 금리
    @JsonProperty("intr_rate2")
    private Double intrRate2;

    // 편의를 위한 메서드: 금리 유형이 단리인지 확인
    public boolean isSingleRate() {
        return "S".equals(intrRateType);
    }

    // 편의를 위한 메서드: 금리 유형이 복리인지 확인
    public boolean isCompoundRate() {
        return "M".equals(intrRateType);
    }
}