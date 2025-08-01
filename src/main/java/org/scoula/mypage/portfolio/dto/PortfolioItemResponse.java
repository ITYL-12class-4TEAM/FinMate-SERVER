package org.scoula.mypage.portfolio.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "포트폴리오 항목 응답 DTO")
public class PortfolioItemResponse {

    @ApiModelProperty(value = "포트폴리오 ID", example = "1")
    private Long portfolioId;

    @ApiModelProperty(value = "사용자 ID", example = "1001")
    private Long memberId;

    @ApiModelProperty(value = "대분류", example = "예금")
    private String category;

    @ApiModelProperty(value = "소분류", example = "정기예금")
    private String subcategory;

    @ApiModelProperty(value = "직접 입력한 상품명", example = "우리은행 정기예금")
    private String customProductName;

    @ApiModelProperty(value = "직접 입력한 금융사명", example = "우리은행")
    private String customCompanyName;

    @ApiModelProperty(value = "투자 금액", example = "5000000")
    private Long amount;

    @ApiModelProperty(value = "가입일", example = "2025-07-31")
    private String joinDate;

    @ApiModelProperty(value = "만기일", example = "2026-07-31")
    private String maturityDate;

    @ApiModelProperty(value = "가입 기간 (개월)", example = "12")
    private Integer saveTrm;

    @ApiModelProperty(value = "금리 (%)", example = "3.5")
    private Double interestRate;

    @ApiModelProperty(value = "예상 수익률 (%)", example = "4.2")
    private Double expectedReturn;

    @ApiModelProperty(value = "우대 금리 (%)", example = "3.7")
    private Double customRate;

    @ApiModelProperty(value = "리스크 수준", example = "LOW")
    private String riskLevel;

    @ApiModelProperty(value = "사용자 메모", example = "3년간 묵히기")
    private String memo;

    @ApiModelProperty(value = "생성 시각", example = "2025-07-31T12:00:00")
    private String createdAt;

    @ApiModelProperty(value = "수정 시각", example = "2025-07-31T12:10:00")
    private String updatedAt;

    @ApiModelProperty(value = "예상 이자 수익 (원)", example = "120000")
    private Long estimatedInterest;

    @ApiModelProperty(value = "세후 총 수령액 (원)", example = "5120000")
    private Long estimatedAfterTax;
}
