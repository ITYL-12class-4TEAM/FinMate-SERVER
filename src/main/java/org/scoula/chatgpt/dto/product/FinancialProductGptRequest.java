package org.scoula.chatgpt.dto.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "금융 상품 정보 요청 DTO")
@Data
public class FinancialProductGptRequest {
    @ApiModelProperty(value = "상품명", example = "Sh첫만남우대예금")
    private String name;

    @ApiModelProperty(value = "카테고리", example = "예적금")
    private String category;

    @ApiModelProperty(value = "은행명", example = "SH수협은행")
    private String bank;

    @ApiModelProperty(value = "금리", example = "1.85% (12개월), 최고 2.900%(12개월)")
    private String interestRate;

    @ApiModelProperty(value = "기간", example = "1년")
    private String period;

    @ApiModelProperty(value = "금액", example = "100만원이상 1,000만원이하")
    private String amount;

    @ApiModelProperty(value = "대상", example = "실명의 개인(1인 1계좌)")
    private String target;

    @ApiModelProperty(value = "우대사항", example = "첫거래")
    private String benefits;

    @Override
    public String toString() {
        return String.format(
                "%s\n카테고리: %s\n은행: %s\n금리: %s\n기간: %s\n금액: %s\n대상: %s\n우대: %s",
                name, category, bank, interestRate, period, amount, target, benefits
        );
    }
}
