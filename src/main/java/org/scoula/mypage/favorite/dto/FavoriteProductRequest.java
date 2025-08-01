package org.scoula.mypage.favorite.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@ApiModel(description = "관심상품 등록 또는 삭제 요청 DTO")
public class FavoriteProductRequest {

    @NotNull(message = "상품 ID는 필수입니다.")
    @ApiModelProperty(value = "등록 또는 삭제할 상품의 ID", example = "101", required = true)
    private Long productId;

    @Min(value = 1, message = "예치 기간은 최소 1개월 이상이어야 합니다.")
    @Max(value = 36, message = "예치 기간은 최대 36개월까지 가능합니다.")
    @ApiModelProperty(value = "희망 예치 기간 (개월)", example = "12")
    private int saveTrm;

    @Pattern(regexp = "S|F", message = "rsrvType은 'S' 또는 'F'만 허용됩니다.")
    @ApiModelProperty(value = "적립 유형 코드 ('S': 정액적립, 'F': 자유적립)", example = "S")
    private String rsrvType;
}
