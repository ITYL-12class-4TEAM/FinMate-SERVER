package org.scoula.products.dto.response.deposit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * 정기예금 상품 정보 DTO
 * 금융감독원 금융상품 한눈에 API 기준으로 구성
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DepositProductDTO {

    // 상품 ID (예: product_id)
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

    // 우대조건 내용 파싱한 태그
    @JsonProperty("preferential_tags")
    private String preferentialTags;

    // 가입대상
    @JsonProperty("join_member")
    private String joinMember;

    // 가입 제한
    @JsonProperty("join_deny")
    private String joinDeny;

    // 최소 가입 금액
    @JsonProperty("minDepositAmount")
    private Long joinAmt;  // 필드명은 그대로 두고 JSON 속성명만 변경

    // 최대 가입 금액 (예금은 납입 전부, 적금은 월별 납입)
    @JsonProperty("maxDepositAmount")
    private Long maxLimit;  // 필드명은 그대로 두고 JSON 속성명만 변경

    // 공시 시작일
    @JsonProperty("dcls_strt_day")
    private String dclsStrtDay;

    // 공시 종료일
    @JsonProperty("dcls_end_day")
    private String dclsEndDay;

    @JsonProperty("company_url")
    private String companyUrl;

    // 금리 정보 목록
    @JsonProperty("options")
    private List<DepositOptionDTO> options;


    // 편의를 위한 메서드: 상품 시작일을 LocalDate로 변환
    public LocalDate getStartDate() {
        if (dclsStrtDay == null || dclsStrtDay.isEmpty()) {
            return null;
        }

        try {
            // 기본 ISO 형식 시도 (yyyy-MM-dd)
            return LocalDate.parse(dclsStrtDay);
        } catch (DateTimeParseException e) {
            try {
                // 명시적인 yyyy-MM-dd 형식 시도
                return LocalDate.parse(dclsStrtDay, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e1) {
                try {
                    // yyyyMMdd 형식 시도
                    return LocalDate.parse(dclsStrtDay, DateTimeFormatter.ofPattern("yyyyMMdd"));
                } catch (DateTimeParseException e2) {
                    // 모든 시도 실패 시 로그 출력 및 null 반환
                    System.err.println("날짜 파싱 실패: " + dclsStrtDay);
                    return null;
                }
            }
        }
    }

    // 편의를 위한 메서드: 상품 종료일을 LocalDate로 변환
    public LocalDate getEndDate() {
        if (dclsEndDay == null || dclsEndDay.isEmpty()) {
            return null;
        }

        try {
            // 기본 ISO 형식 시도 (yyyy-MM-dd)
            return LocalDate.parse(dclsEndDay);
        } catch (DateTimeParseException e) {
            try {
                // 명시적인 yyyy-MM-dd 형식 시도
                return LocalDate.parse(dclsEndDay, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e1) {
                try {
                    // yyyyMMdd 형식 시도
                    return LocalDate.parse(dclsEndDay, DateTimeFormatter.ofPattern("yyyyMMdd"));
                } catch (DateTimeParseException e2) {
                    // 모든 시도 실패 시 로그 출력 및 null 반환
                    System.err.println("날짜 파싱 실패: " + dclsEndDay);
                    return null;
                }
            }
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