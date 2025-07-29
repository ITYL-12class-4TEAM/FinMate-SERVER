package org.scoula.mypage.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "포트폴리오 요약 + 비교 통계 응답 DTO")
public class PortfolioSummaryResponseDTO {

    @ApiModelProperty(value = "내 포트폴리오 요약 목록")
    private List<PortfolioSummaryDTO> mySummary;

    @ApiModelProperty(value = "비교 통계 요약 정보")
    private PortfolioComparisonDTO comparisonSummary;
}

