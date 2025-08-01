package org.scoula.mypage.portfolio.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "포트폴리오 수정 요청 DTO")
public class PortfolioUpdateRequest {

    @ApiModelProperty(value = "대분류 (예: 예금, 적금 등)", example = "예금")
    private String category;

    @ApiModelProperty(value = "소분류 (예: 정기예금, 연금저축 등)", example = "정기예금")
    private String subcategory;

    @ApiModelProperty(value = "사용자 입력 상품명", example = "KB정기예금")
    private String customProductName;

    @ApiModelProperty(value = "사용자 입력 금융사명", example = "국민은행")
    private String customCompanyName;

    @ApiModelProperty(value = "수정할 투자 금액", example = "3000000")
    private Long amount;

    @ApiModelProperty(value = "가입일 (yyyy-MM-dd)", example = "2025-08-01")
    private String joinDate;

    @ApiModelProperty(value = "만기일 (yyyy-MM-dd)", example = "2026-08-01")
    private String maturityDate;

    @ApiModelProperty(value = "가입 기간 (개월)", example = "12")
    private Integer saveTrm;

    @ApiModelProperty(value = "금리 (%)", example = "3.5")
    private Double interestRate;

    @ApiModelProperty(value = "예상 수익률 (%)", example = "4.2")
    private Double expectedReturn;

    @ApiModelProperty(value = "사용자 입력 우대 금리 (%)", example = "3.7")
    private Double customRate;

    @ApiModelProperty(value = "리스크 수준", example = "LOW")
    private String riskLevel;

    @ApiModelProperty(value = "메모", example = "단기 상품으로 변경 예정")
    private String memo;
}
