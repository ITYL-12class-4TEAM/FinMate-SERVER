package org.scoula.mypage.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "포트폴리오 비교 통계 정보 DTO")
public class PortfolioComparisonDTO {

    @ApiModelProperty(value = "연령대별 평균 비율")
    private List<CategoryRatioDTO> byAgeGroup;

    @ApiModelProperty(value = "유사 투자금액 그룹 평균 비율")
    private List<CategoryRatioDTO> byAmountGroup;

    @ApiModelProperty(value = "투자성향(WMTI) 기반 평균 비율")
    private List<CategoryRatioDTO> byWMTI;
}
