package org.scoula.products.dto.response.pension;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 연금저축 상품 옵션 정보 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PensionOptionDTO {

    // 옵션 ID
    @JsonProperty("option_id")
    private Long optionId;

    // 상품 ID (외래키)
    @JsonProperty("product_id")
    private Long productId;

    // 연금 수령 기간 코드
    @JsonProperty("pnsn_recp_trm")
    private String pnsnRecpTrm;

    // 연금 수령 기간명
    @JsonProperty("pnsn_recp_trm_nm")
    private String pnsnRecpTrmNm;

    // 연금 가입 연령
    @JsonProperty("pnsn_entr_age")
    private Integer pnsnEntrAge;

    // 연금 가입 연령명
    @JsonProperty("pnsn_entr_age_nm")
    private String pnsnEntrAgeNm;

    // 월 납입금액
    @JsonProperty("mon_paym_atm")
    private Integer monPaymAtm;

    // 월 납입금액명
    @JsonProperty("mon_paym_atm_nm")
    private String monPaymAtmNm;

    // 납입 기간
    @JsonProperty("paym_prd")
    private Integer paymPrd;

    // 납입 기간명
    @JsonProperty("paym_prd_nm")
    private String paymPrdNm;

    // 연금 시작 연령
    @JsonProperty("pnsn_strt_age")
    private Integer pnsnStrtAge;

    // 연금 시작 연령명
    @JsonProperty("pnsn_strt_age_nm")
    private String pnsnStrtAgeNm;

    // 연금 수령액
    @JsonProperty("pnsn_recp_amt")
    private Long pnsnRecpAmt;

    @JsonProperty("fin_prdt_cd")
    private String finPrdtCd;
}