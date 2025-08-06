package org.scoula.mypage.portfolio.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "카테고리별 평균 비율 정보 DTO")
public class CategoryRatioResponse {

    @ApiModelProperty(value = "서브카테고리 이름", example = "정기예금")
    private String categoryName;

    @ApiModelProperty(value = "평균 비율 (%)", example = "37.5")
    private Double averageRatio;
}
