package org.scoula.mypage.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "포트폴리오 수정 요청 DTO")
public class PortfolioUpdateDTO {

    @ApiModelProperty(value = "수정할 투자 금액", example = "3000000")
    private Long amount;

    @ApiModelProperty(value = "수정할 메모", example = "단기 상품으로 변경 예정")
    private String memo;
}
