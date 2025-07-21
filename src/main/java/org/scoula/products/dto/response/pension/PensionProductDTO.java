package org.scoula.products.dto.response.pension;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 연금저축 상품 정보 DTO
 * 금융감독원 금융상품 한눈에 API 기준으로 구성
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PensionProductDTO {

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

    // 가입 방법
    @JsonProperty("join_way")
    private String joinWay;

    // 연금수령 방식
    @JsonProperty("pnsn_rcv_mthd")
    private String pnsnRcvMthd;

    // 월 납입액
    @JsonProperty("mnth_pym_atm")
    private Long mnthPymAtm;

    // 연금 가입연령
    @JsonProperty("pnsn_strt_age")
    private Integer pnsnStrtAge;

    // 연금 수령연령
    @JsonProperty("pnsn_recp_trm")
    private String pnsnRecpTrm;

    // 상품설명
    @JsonProperty("etc_note")
    private String etcNote;

    // 공시 시작일
    @JsonProperty("dcls_strt_day")
    private String dclsStrtDay;

    // 공시 종료일
    @JsonProperty("dcls_end_day")
    private String dclsEndDay;

    // 금리 정보 목록
    @JsonProperty("options")
    private List<PensionOptionDTO> options;

    // 편의를 위한 메서드: 상품 시작일을 LocalDate로 변환
    public LocalDate getStartDate() {
        if (dclsStrtDay == null || dclsStrtDay.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dclsStrtDay, DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    // 편의를 위한 메서드: 상품 종료일을 LocalDate로 변환
    public LocalDate getEndDate() {
        if (dclsEndDay == null || dclsEndDay.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dclsEndDay, DateTimeFormatter.ofPattern("yyyyMMdd"));
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