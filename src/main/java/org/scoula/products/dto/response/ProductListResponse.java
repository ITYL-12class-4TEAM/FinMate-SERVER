package org.scoula.products.dto.response;

import lombok.Data;
import java.util.List;

/**
 * 금융상품 목록 조회 응답 DTO
 * 상품 검색 결과 목록과 페이징 정보를 포함합니다.
 */
@Data
public class ProductListResponse {
    /** 상품 목록 */
    private List<ProductSummary> products;

    /** 전체 상품 수 */
    private int totalCount;

    /** 현재 페이지 번호 */
    private int pageNo;

    /** 전체 페이지 수 */
    private int totalPages;

    /**
     * 상품 요약 정보
     * 목록 조회 시 각 상품의 기본 정보를 담는 내부 클래스입니다.
     */
    @Data
    public static class ProductSummary {
        /** 상품 ID */
        private String productId;

        /** 금융회사 ID */
        private String companyId;

        /** 상품명 */
        private String productName;

        /** 금융회사명 */
        private String companyName;

        /** 기본 금리 (%) */
        private Double interestRate;

        /** 우대 금리 (%) */
        private Double specialRate;

        /** 저축 기간 (개월) */
        private Integer saveTerm;

        /** 가입 방법 (온라인, 방문 등) */
        private String joinMethod;
    }
}