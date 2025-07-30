package org.scoula.products.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 금융상품 목록 조회 응답 DTO
 * 상품 검색 결과 목록과 페이징 정보를 포함합니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponse {
    // 호환성을 위해 유지
    private String productType;

    // 카테고리 및 서브카테고리 정보 추가
    private Long categoryId;
    private Long subcategoryId;

    // 기존 필드
    private List<ProductSummary> products; // 예금/적금 상품 요약
    private List<PensionProductSummary> pensionProducts; // 연금저축 상품 요약
    private int totalCount;
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private String sortBy;
    private String sortDirection;

    /**
     * deposit 상품 요약 정보
     * 목록 조회 시 각 상품의 기본 정보를 담는 내부 클래스
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductSummary {
        private Long productId;      // 상품 ID (예: product_id)
        private String finPrdtCd;      // 상품 코드
        private String korCoNm;        // 은행명
        private String finPrdtNm;      // 상품명
        private Double intrRate;       // 기본 금리
        private Double intrRate2;      // 우대 금리
        private Integer saveTrm;       // 저축 기간
        private String joinWay;        // 가입 방법

        // 확장된 필드
        private String intrRateType;   // 금리 유형 (S: 단리, M: 복리)
        private Long minDepositAmount; // 최소 예치 금액
    }

    /**
     * pension 상품 요약 정보
     * 목록 조회 시 각 상품의 기본 정보를 담는 내부 클래스
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PensionProductSummary {
        private String finPrdtCd;
        private String korCoNm;
        private String finPrdtNm;
        private Double dclsRate;    // 공시이율
        private Double guarRate;    // 최저보증이율
        private String pnsnKind;    // 연금 종류
        private String pnsnKindNm;  // 연금 종류명
        private String prdtType;    // 상품 유형
        private String prdtTypeNm;  // 상품 유형명
    }
}