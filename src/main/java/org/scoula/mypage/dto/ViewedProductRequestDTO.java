package org.scoula.mypage.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "최근 본 상품 등록 요청 DTO")
public class ViewedProductRequestDTO {

    @ApiModelProperty(value = "조회한 상품 ID", example = "101", required = true)
    private Long productId;
}
