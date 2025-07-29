package org.scoula.mypage.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "포트폴리오 등록 요청 DTO")
public class PortfolioCreateDTO {

    @ApiModelProperty(value = "상품 ID", example = "101", required = true)
    private Long productId;

    @ApiModelProperty(value = "카테고리 ID", example = "1", required = true)
    private Long categoryId;

    @ApiModelProperty(value = "서브카테고리 ID", example = "10")
    private Long subcategoryId;

    @ApiModelProperty(value = "투자 금액", example = "5000000", required = true)
    private Long amount;

    @ApiModelProperty(value = "가입일 (yyyy-MM-dd)", example = "2025-07-24", required = true)
    private String joinDate;

    @ApiModelProperty(value = "메모", example = "3년간 유지 예정")
    private String memo;

    @ApiModelProperty(value = "사용자 입력 우대 금리 (%)", example = "3.7")
    private Double customRate;

    @ApiModelProperty(value = "가입 기간 (개월)", example = "12")
    private Integer saveTrm;

}
