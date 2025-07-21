package org.scoula.products.dto.response.saving;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 적금 상품 정보 DTO
 * 금융감독원 금융상품 한눈에 API 기준으로 구성
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingProductDTO {

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

    // 가입 방법
    @JsonProperty("join_way")
    private String joinWay;

    // 만기 후 이자율 유형
    @JsonProperty("mtrt_int")
    private String mtrtInt;

    // 상품설명
    @JsonProperty("etc_note")
    private String etcNote;

    // 우대조건
    @JsonProperty("spcl_cnd")
    private String spclCnd;

    // 가입대상
    @JsonProperty("join_member")
    private String joinMember;

    // 가입 제한
    @JsonProperty("join_deny")
    private String joinDeny;

    // 최소 가입 금액
    @JsonProperty("join_amt")
    private Long joinAmt;

    // 공시 시작일
    @JsonProperty("dcls_strt_day")
    private String dclsStrtDay;

    // 공시 종료일
    @JsonProperty("dcls_end_day")
    private String dclsEndDay;

    // 적립 유형 (자유적립식/정액적립식)
    @JsonProperty("rsrv_type")
    private String rsrvType;

    // 적립 유형명
    @JsonProperty("rsrv_type_nm")
    private String rsrvTypeNm;

    // 금리 정보 목록
    @JsonProperty("options")
    private List<SavingOptionDTO> options;

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

    // 편의를 위한 메서드: 자유적립식인지 확인
    public boolean isFreeDeposit() {
        return "F".equals(rsrvType);
    }

    // 편의를 위한 메서드: 정액적립식인지 확인
    public boolean isFixedDeposit() {
        return "S".equals(rsrvType);
    }
}