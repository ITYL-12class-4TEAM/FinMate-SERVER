package org.scoula.mypage.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

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

    private String categoryName;       // 예: 예금, 적금, 연금
    private String subcategoryName;    // 예: 자유적금, 청년적금 등

    private BigDecimal baseRate;       // 기본 금리 (intr_rate 또는 dcls_rate)
    private BigDecimal maxRate;        // 우대 금리 (deposit only, optional)
    private int saveTrm;          // 희망 예치 기간 (개월 단위, 1, 3, 6, 12, 24, 36달 등)

    private String rstvTypeName; // "S" (정액적립), "F" (자유 적립)
    private String rstvValue;
    private String prdtTypeName; // 연금 "금리연동형", "주식형" 등
}
