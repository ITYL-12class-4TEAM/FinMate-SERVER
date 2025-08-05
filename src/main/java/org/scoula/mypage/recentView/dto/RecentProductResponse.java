package org.scoula.mypage.recentView.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "최근 본 상품 응답 DTO")
public class RecentProductResponse {

    @ApiModelProperty(value = "상품 ID", example = "101")
    private Long productId;

    @ApiModelProperty(value = "상품 이름", example = "청년우대형 정기예금")
    private String productName;

    @ApiModelProperty(value = "금융회사 이름", example = "KB국민은행")
    private String korCoNm;

    @ApiModelProperty(value = "상품 카테고리", example = "예금")
    private String categoryName;

    @ApiModelProperty(value = "상품 세부 카테고리", example = "정기예금")
    private String subcategoryName;

    @ApiModelProperty(value = "최고 금리", example = "3.2")
    private Double maxRate;

    @ApiModelProperty(value = "찜한 사람 수", example = "120")
    private Integer wishlistCount;

    @ApiModelProperty(value = "조회 시각 (yyyy-MM-dd HH:mm:ss)", example = "2025-07-24 10:30:00")
    private String viewedAt;

    @ApiModelProperty(value = "상품 유형  (S: 단리 M:복리)", example = "S")
    private String intrRateType;

    @ApiModelProperty(value = "적립 유형 코드 이름 (예: S=정액적립, F=자유적립)", example = "S")
    private String rstvTypeName;

    @ApiModelProperty(value = "적립 방식 세부 설명", example = "정액적립식")
    private String rstvValue;

    @ApiModelProperty(value = "예치 기간 (개월 단위)", example = "12")
    private Integer saveTrm;
}