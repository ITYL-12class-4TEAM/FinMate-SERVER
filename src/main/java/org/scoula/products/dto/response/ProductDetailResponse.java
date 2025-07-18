package org.scoula.products.dto.response;

import lombok.Data;
import java.util.List;

/**
 * 금융상품 상세 정보 응답 DTO
 * 특정 상품의 모든 상세 정보와 금리 옵션을 포함합니다.
 */
@Data
public class ProductDetailResponse {
    /** 상품 ID */
    private String productId;

    /** 금융회사 ID */
    private String companyId;

    /** 상품명 */
    private String productName;

    /** 금융회사명 */
    private String companyName;

    /** 상품 유형 (정기예금, 적금, 연금저축 등) */
    private String productType;

    /** 가입 방법 (온라인, 방문 등) */
    private String joinMethod;

    /** 우대 조건 */
    private String specialCondition;

    /** 만기 후 이자율 */
    private String maturityInterest;

    /** 금리 옵션 목록 */
    private List<InterestOption> interestOptions;

    /**
     * 금리 옵션 정보
     * 상품의 기간별, 유형별 금리 정보를 담는 내부 클래스입니다.
     */
    @Data
    public static class InterestOption {
        /** 금리 유형 (단리, 복리 등) */
        private String interestRateType;

        /** 저축 기간 (개월) */
        private Integer saveTerm;

        /** 기본 금리 (%) */
        private Double interestRate;

        /** 우대 금리 (%) */
        private Double specialRate;
    }
}