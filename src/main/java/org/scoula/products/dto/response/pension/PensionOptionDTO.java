package org.scoula.products.dto.response.pension;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PensionOptionDTO {

    // 금융상품 코드
    @JsonProperty("fin_prdt_cd")
    private String finPrdtCd;

    // 연금 수령 기간
    @JsonProperty("pnsn_recp_trm")
    private String pnsnRecpTrm;

    // 연금 수령 기간명
    @JsonProperty("pnsn_recp_trm_nm")
    private String pnsnRecpTrmNm;

    // 연금 가입 나이
    @JsonProperty("pnsn_entr_age")
    private Integer pnsnEntrAge;

    // 연금 가입 나이명
    @JsonProperty("pnsn_entr_age_nm")
    private String pnsnEntrAgeNm;

    // 월 납입액
    @JsonProperty("mon_paym_atm")
    private Integer monPaymAtm;

    // 월 납입액명
    @JsonProperty("mon_paym_atm_nm")
    private String monPaymAtmNm;

    // 납입 기간
    @JsonProperty("paym_prd")
    private Integer paymPrd;

    // 납입 기간명
    @JsonProperty("paym_prd_nm")
    private String paymPrdNm;

    // 연금 시작 나이
    @JsonProperty("pnsn_strt_age")
    private Integer pnsnStrtAge;

    // 연금 시작 나이명
    @JsonProperty("pnsn_strt_age_nm")
    private String pnsnStrtAgeNm;

    // 연금 수령액
    @JsonProperty("pnsn_recp_amt")
    private Long pnsnRecpAmt;

    // 아래 필드들은 제거하거나 주석 처리
    // @JsonProperty("save_trm")
    // private Integer saveTrm;

    // @JsonProperty("intr_rate_type")
    // private String intrRateType;

    // @JsonProperty("intr_rate_type_nm")
    // private String intrRateTypeNm;

    // @JsonProperty("intr_rate")
    // private Double intrRate;

    // @JsonProperty("intr_rate2")
    // private Double intrRate2;

    // 편의 메서드도 필요에 따라 수정/제거
    // public boolean isSingleRate() {
    //     return "S".equals(intrRateType);
    // }

    // public boolean isCompoundRate() {
    //     return "M".equals(intrRateType);
    // }
}