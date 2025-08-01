package org.scoula.mypage.portfolio.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "연령대별 사용자 수 DTO")
public class AgeGroupStatResponse {
    @ApiModelProperty(value = "연령대 (예: 20대, 30대)")
    private String ageGroup;

    @ApiModelProperty(value = "해당 연령대 사용자 수", example = "15")
    private long userCount;
}
