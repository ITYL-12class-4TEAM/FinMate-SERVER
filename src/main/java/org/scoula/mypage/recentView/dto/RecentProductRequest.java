package org.scoula.mypage.recentView.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
@ApiModel(description = "최근 본 상품 등록 요청 DTO")
public class RecentProductRequest {

    @ApiModelProperty(value = "조회한 상품 ID", example = "101", required = true)
    private Long productId;

    @ApiModelProperty(value = "조회한 상품의 예치 기간 (개월)", example = "12")
    private Integer saveTrm; // 저장 기간

    @Pattern(regexp = "S|M", message = "intr_rate_type은 'S' 또는 'M'만 허용됩니다.")
    @ApiModelProperty(value = "적립 유형 코드 ('S': 단리, 'M': 복리)", example = "S")
    private String intrRateType;

    @ApiModelProperty(value = "적립 방식", example = "S", allowableValues = "S,F")
    private String rsrvType; // "S"(정액), "F"(자유)
}
