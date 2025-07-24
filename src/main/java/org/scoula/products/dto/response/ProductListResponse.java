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
    private List<ProductSummary> products;
    private int totalCount;
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private String sortBy;
    private String sortDirection;

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