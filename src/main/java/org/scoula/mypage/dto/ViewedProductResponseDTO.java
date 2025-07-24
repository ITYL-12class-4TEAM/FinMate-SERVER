package org.scoula.mypage.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "최근 본 상품 응답 DTO")
public class ViewedProductResponseDTO {

    @ApiModelProperty(value = "상품 ID", example = "101")
    private Long productId;

    @ApiModelProperty(value = "상품 이름", example = "청년우대형 정기예금")
    private String productName;

    @ApiModelProperty(value = "금융회사 이름", example = "KB국민은행")
    private String korCoNm;

    @ApiModelProperty(value = "위험도 (LOW, MEDIUM, HIGH)", example = "LOW")
    private String riskLevel;

    @ApiModelProperty(value = "상품 외부 링크", example = "https://bank.example.com/products/101")
    private String externalLink;

    @ApiModelProperty(value = "찜한 사람 수", example = "120")
    private Integer wishlistCount;

    @ApiModelProperty(value = "조회 시각 (yyyy-MM-dd HH:mm:ss)", example = "2025-07-24 10:30:00")
    private String viewedAt;
}
