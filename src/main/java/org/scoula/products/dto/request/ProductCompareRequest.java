package org.scoula.products.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 금융 상품 비교 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCompareRequest {

    // 상품 유형 (deposit: 예금, saving: 적금, pension: 연금저축)
    @NotEmpty(message = "상품 유형은 필수입니다.")
    @Pattern(regexp = "deposit|saving|pension", message = "상품 유형은 deposit, saving, pension 중 하나여야 합니다.")
    private String productType;

    // 비교할 상품 코드 목록
    @NotEmpty(message = "비교할 상품 코드는 최소 2개 이상이어야 합니다.")
    @Size(min = 2, max = 3, message = "비교할 상품 코드는 2개 이상 3개 이하여야 합니다.")
    private List<String> productCodes;

    // 저축 기간 (개월 수)
    private Integer saveTrm;
}