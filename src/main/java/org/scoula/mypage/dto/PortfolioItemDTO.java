package org.scoula.mypage.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "사용자의 포트폴리오 항목 정보 DTO")
public class PortfolioItemDTO {

    @ApiModelProperty(value = "포트폴리오 항목 ID", example = "1001")
    private Long portfolioId;

    @ApiModelProperty(value = "상품 이름", example = "KB국민 정기예금")
    private String productName;

    @ApiModelProperty(value = "카테고리 이름", example = "예금")
    private String categoryName;

    @ApiModelProperty(value = "서브카테고리 이름", example = "정기예금")
    private String subcategoryName;

    @ApiModelProperty(value = "투자 금액", example = "5000000")
    private Long amount;

    @ApiModelProperty(value = "가입일 (yyyy-MM-dd)", example = "2024-08-01")
    private String joinDate;

    @ApiModelProperty(value = "메모", example = "장기 보유 예정")
    private String memo;
}
