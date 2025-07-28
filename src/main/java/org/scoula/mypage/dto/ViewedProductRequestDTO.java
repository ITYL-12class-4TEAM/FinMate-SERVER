package org.scoula.mypage.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "최근 본 상품 등록 요청 DTO")
public class ViewedProductRequestDTO {

    @ApiModelProperty(value = "조회한 상품 ID", example = "101", required = true)
    private Long productId;

    @ApiModelProperty(value = "조회한 상품의 예치 기간 (개월)", example = "12")
    private Integer saveTrm; // 저장 기간

    @ApiModelProperty(value = "적립 방식", example = "S", allowableValues = "S,F")
    private String rsrvType; // "S"(정액), "F"(자유)
}
