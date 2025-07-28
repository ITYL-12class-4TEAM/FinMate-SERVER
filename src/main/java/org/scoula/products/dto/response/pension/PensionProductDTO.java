package org.scoula.products.dto.response.pension;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 연금저축 상품 정보 DTO
 * 금융감독원 금융상품 한눈에 API 기준으로 구성
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PensionProductDTO {
    // 상품 ID (primary key)
    @JsonProperty("product_id")
    private Long productId;

    // 금융회사 코드
    @JsonProperty("fin_co_no")
    private String finCoNo;

    // 금융회사명
    @JsonProperty("kor_co_nm")
    private String korCoNm;

    // 금융상품 코드
    @JsonProperty("fin_prdt_cd")
    private String finPrdtCd;

    // 금융 상품명
    @JsonProperty("fin_prdt_nm")
    private String finPrdtNm;

    // 연금저축 상품 유형
    @JsonProperty("pnsn_kind")
    private String pnsnKind;

    // 연금저축 상품 유형명
    @JsonProperty("pnsn_kind_nm")
    private String pnsnKindNm;

    // 상품 유형 코드 (추가)
    @JsonProperty("prdt_type")
    private String prdtType;

    // 상품 유형명 (추가)
    @JsonProperty("prdt_type_nm")
    private String prdtTypeNm;

    // 위험도 카테고리 (추가)
    @JsonProperty("category")
    private String category;

    // 가입 방법
    @JsonProperty("join_way")
    private String joinWay;

    // 연금수령 방식
    @JsonProperty("pnsn_rcv_mthd")
    private String pnsnRcvMthd;

    // 월 납입액
    @JsonProperty("mnth_pym_atm")
    private Long mnthPymAtm;

    // 연금 가입 가능 연령 (30세)
    @JsonProperty("pnsn_entr_age")
    private Integer pnsnEntrAge;

    // 연금 수령 시작 연령 (60세/65세 등)
    @JsonProperty("pnsn_strt_age")
    private Integer pnsnStrtAge;

    // 연금 수령 기간 코드 (예: A, B 등)
    @JsonProperty("pnsn_recp_trm")
    private String pnsnRecpTrm;

    // 연금 수령 기간 이름 (예: "10년 수령", "종신형" 등)
    @JsonProperty("pnsn_recp_trm_nm")
    private String pnsnRecpTrmNm;

    // 기타 상품설명
    @JsonProperty("etc_note")
    private String etcNote;

    // 공시 시작일
    @JsonProperty("dcls_strt_day")
    private String dclsStrtDay;

    // 공시 종료일
    @JsonProperty("dcls_end_day")
    private String dclsEndDay;

    // 공시 수익율 (수정: 테이블의 dcls_rate 필드와 매핑)
    @JsonProperty("dcls_rate")
    private Double dclsRate;

    // 최저보증이율 (수정: 테이블의 guar_rate 필드와 매핑)
    @JsonProperty("guar_rate")
    private Double guarRate;

    // 1년 수익률 (추가: 테이블의 btrm_prft_rate_1 필드와 매핑)
    @JsonProperty("profit_rate_1")
    private Double profitRate1;

    // 2년 수익률 (추가: 테이블의 btrm_prft_rate_2 필드와 매핑)
    @JsonProperty("profit_rate_2")
    private Double profitRate2;

    // 3년 수익률 (추가: 테이블의 btrm_prft_rate_3 필드와 매핑)
    @JsonProperty("profit_rate_3")
    private Double profitRate3;

    // 최소 가입 연령 (추가: 옵션 테이블에서 집계)
    @JsonProperty("min_entry_age")
    private Integer minEntryAge;

    // 최대 가입 연령 (추가: 옵션 테이블에서 집계)
    @JsonProperty("max_entry_age")
    private Integer maxEntryAge;

    // 최소 납입금액 (추가: 옵션 테이블에서 집계)
    @JsonProperty("min_payment")
    private Integer minPayment;

    // 최대 납입금액 (추가: 옵션 테이블에서 집계)
    @JsonProperty("max_payment")
    private Integer maxPayment;

    // 금리 정보 목록
    @JsonProperty("options")
    private List<PensionOptionDTO> options = new ArrayList<>();

    // 뷰에서 필요한 추가 필드 (API 응답용)
    @JsonIgnore
    private List<Map<String, Object>> additionalOptions;

    // 편의를 위한 메서드: 상품 시작일을 LocalDate로 변환
    public LocalDate getStartDate() {
        if (dclsStrtDay == null || dclsStrtDay.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dclsStrtDay, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            return null;
        }
    }

    // 편의를 위한 메서드: 상품 종료일을 LocalDate로 변환
    public LocalDate getEndDate() {
        if (dclsEndDay == null || dclsEndDay.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dclsEndDay, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            return null;
        }
    }

    // 편의를 위한 메서드: 상품이 현재 판매 중인지 확인
    public boolean isAvailable() {
        if (dclsEndDay == null || dclsEndDay.isEmpty()) {
            return true;
        }
        LocalDate endDate = getEndDate();
        return endDate == null || endDate.isAfter(LocalDate.now());
    }
}