package org.scoula.mypage.favorite.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(description = "사용자가 관심 상품으로 등록한 금융 상품 정보")
public class FavoriteProductResponse {

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

    @ApiModelProperty(value = "카테고리 이름 (예: 예금, 적금, 연금)", example = "적금")
    private String categoryName;

    @ApiModelProperty(value = "서브카테고리 이름 (예: 자유적금, 청년적금)", example = "자유적금")
    private String subcategoryName;

    @ApiModelProperty(value = "기본 금리", example = "2.25")
    private BigDecimal baseRate;

    @ApiModelProperty(value = "우대 금리", example = "3.00")
    private BigDecimal maxRate;

    @ApiModelProperty(value = "예치 기간 (개월 단위)", example = "12")
    private Integer saveTrm;

    @ApiModelProperty(value = "적립 유형 코드 이름 (예: S=정액적립, F=자유적립)", example = "S")
    private String rstvTypeName;

    @ApiModelProperty(value = "적립 방식 세부 설명", example = "정액적립식")
    private String rstvValue;

    @ApiModelProperty(value = "상품 유형 (예: 금리연동형, 주식형)", example = "금리연동형")
    private String prdtTypeName;

}
