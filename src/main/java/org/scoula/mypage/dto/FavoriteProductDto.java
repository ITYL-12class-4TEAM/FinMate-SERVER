package org.scoula.mypage.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "사용자가 관심 상품으로 등록한 금융 상품 정보")
public class FavoriteProductDto {

    @ApiModelProperty(value = "상품 ID", example = "101")
    private Long productId;

    @ApiModelProperty(value = "상품 이름", example = "청년우대형 주택청약통장")
    private String productName;

    @ApiModelProperty(value = "금융회사 이름", example = "국민은행")
    private String korCoNm;

    @ApiModelProperty(value = "위험도 (LOW, MEDIUM, HIGH)", example = "LOW")
    private String riskLevel;

    @ApiModelProperty(value = "상품 외부 링크", example = "https://bank.example.com/product/101")
    private String externalLink;

    @ApiModelProperty(value = "찜한 사람 수", example = "87")
    private Integer wishlistCount;
}
