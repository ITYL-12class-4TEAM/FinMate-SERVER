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
//    private String joinWay;
}