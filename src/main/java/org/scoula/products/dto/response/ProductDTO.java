package org.scoula.products.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 금융 상품 기본 정보 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long productId;
    private String finCoNo;
    private String finPrdtCd;
    private String korCoNm;
    private String productName;  // finPrdtNm에 해당
    private Double intrRate;
    private Double intrRate2;
    private Integer saveTrm;
    private String joinWay;
    private String intrRateType;
    private String intrRateTypeNm;
    private Integer minDepositAmount;
    private Long minDeposit;  // 최소 예치 금액
    private Long maxLimit;    // 최대 예치 금액
    private String rsrvType;  // 적립식 유형 (S: 정액적립식, F: 자유적립식)
    private String rsrvTypeNm;  // 적립식 유형 명칭
    private String companyUrl;      // homp_url - 회사 홈페이지 URL
}