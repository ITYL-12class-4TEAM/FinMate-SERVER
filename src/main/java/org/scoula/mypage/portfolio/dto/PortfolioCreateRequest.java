package org.scoula.mypage.portfolio.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "포트폴리오 등록 요청")
public class PortfolioCreateRequest {


    @ApiModelProperty(value = "대분류 (예: 예금, 적금, 연금 등)", example = "예금", required = true)
    private String category;

    @ApiModelProperty(value = "소분류 (예: 정기예금, 연금저축 등)", example = "정기예금", required = true)
    private String subcategory;

    @ApiModelProperty(value = "사용자 입력 상품명 (선택)", example = "KB정기예금")
    private String customProductName;

    @ApiModelProperty(value = "사용자 입력 금융사명 (선택)", example = "국민은행")
    private String customCompanyName;

    @ApiModelProperty(value = "투자 금액 또는 예치 금액", example = "5000000", required = true)
    private Long amount;

    @ApiModelProperty(value = "가입일 (yyyy-MM-dd)", example = "2025-07-24", required = true)
    private String joinDate;

    @ApiModelProperty(value = "만기일 (yyyy-MM-dd, 선택)", example = "2026-07-24")
    private String maturityDate;

    @ApiModelProperty(value = "가입 기간 (개월)", example = "12")
    private Integer saveTrm;

    @ApiModelProperty(value = "확정 금리 (%)", example = "3.2")
    private Double interestRate;

    @ApiModelProperty(value = "예상 수익률 (%)", example = "5.6")
    private Double expectedReturn;

    @ApiModelProperty(value = "사용자 입력 우대 금리 (%)", example = "3.7")
    private Double customRate;

    @ApiModelProperty(value = "리스크 수준", example = "LOW")
    private String riskLevel;

    @ApiModelProperty(value = "메모", example = "장기 투자 목적입니다")
    private String memo;
}
