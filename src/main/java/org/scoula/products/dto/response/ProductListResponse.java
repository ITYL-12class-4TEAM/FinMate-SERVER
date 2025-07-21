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
    /** 상품 유형 (deposit: 예금, saving: 적금, pension: 연금저축) */
    private String productType;

    /** 상품 목록 */
    private List<ProductSummary> products;

    /** 전체 상품 수 */
    private Integer totalCount;

    /** 현재 페이지 번호 */
    private Integer currentPage;

    /** 페이지 크기 */
    private Integer pageSize;

    /** 전체 페이지 수 */
    private Integer totalPages;

    /** 정렬 기준 */
    private String sortBy;

    /** 정렬 방향 */
    private String sortDirection;

    /** 검색 조건 요약 */
    private String searchSummary;

    /**
     * 상품 요약 정보
     * 목록 조회 시 각 상품의 기본 정보를 담는 내부 클래스입니다.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductSummary {
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
}