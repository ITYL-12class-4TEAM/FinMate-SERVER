package org.scoula.mypage.portfolio.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "포트폴리오 비교 통계 정보 DTO")
public class PortfolioComparisonResponse {

    @ApiModelProperty(value = "연령대별 평균 비율")
    private List<CategoryRatioResponse> byAgeGroup;

    @ApiModelProperty(value = "연령대별 사용자 수")
    private List<AgeGroupStatResponse> ageGroupStats;

    @ApiModelProperty(value = "유사 투자금액 그룹 평균 비율")
    private List<CategoryRatioResponse> byAmountGroup;

    @ApiModelProperty(value = "투자성향(WMTI) 기반 평균 비율")
    private List<CategoryRatioResponse> byWMTI;

}
