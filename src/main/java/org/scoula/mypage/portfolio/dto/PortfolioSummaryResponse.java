package org.scoula.mypage.portfolio.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "카테고리별 포트폴리오 요약 정보 DTO")
public class PortfolioSummaryResponse {

    @ApiModelProperty(value = "카테고리 이름", example = "예금")
    private String categoryName;

    @ApiModelProperty(value = "총 투자 금액", example = "10000000")
    private Long totalAmount;

    @ApiModelProperty(value = "총액 대비 비율 (소수점)", example = "45.0")
    private Double ratio;

    @ApiModelProperty(value = "서브카테고리별 요약 목록")
    private List<SubcategorySummaryResponse> subcategories;
}
