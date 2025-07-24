package org.scoula.mypage.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "관심상품 등록 또는 삭제 요청 DTO")
public class FavoriteRequestDTO {

    @ApiModelProperty(value = "등록 또는 삭제할 상품의 ID", example = "101", required = true)
    private Long productId;

    @ApiModelProperty(value = "희망 예치 기간 (개월)", example = "12")
    private int saveTrm; // 저장 기간 (1, 3, 6, 12, 24, 36달)

    private String rsrvType; // "S", "F"
}
