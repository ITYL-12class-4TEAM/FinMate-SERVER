package org.scoula.mypage.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "서브카테고리별 포트폴리오 요약 정보 DTO")
public class SubcategorySummaryDTO {

    @ApiModelProperty(value = "서브카테고리 이름", example = "자유적금")
    private String subcategoryName;

    @ApiModelProperty(value = "총 투자 금액", example = "2500000")
    private Long totalAmount;

    @ApiModelProperty(value = "총액 대비 비율 (소수점)", example = "25.0")
    private Double ratio;
}
