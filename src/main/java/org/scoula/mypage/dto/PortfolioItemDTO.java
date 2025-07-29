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

    // 🔽 추가 필드들 🔽

    @ApiModelProperty(value = "가입 기간 (개월)", example = "12")
    private Integer saveTerm;

    @ApiModelProperty(value = "사용자 입력 금리 (%)", example = "3.2")
    private Double customRate;

    @ApiModelProperty(value = "예상 세전 이자 (원)", example = "160000")
    private Long estimatedInterest;

    @ApiModelProperty(value = "예상 세후 수령액 (원)", example = "5136800")
    private Long estimatedAfterTax;

    @ApiModelProperty(value = "만기일 (yyyy-MM-dd)", example = "2025-08-01")
    private String maturityDate;
}
