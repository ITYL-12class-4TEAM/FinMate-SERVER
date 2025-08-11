package org.scoula.products.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "상품 하위 카테고리 정보 DTO")
public class SubcategoryDTO {

    @ApiModelProperty(value = "하위 카테고리 ID", example = "101", required = true)
    private Long subCategoryId;

    @ApiModelProperty(value = "부모 카테고리 ID", example = "1", required = true)
    private Long categoryId;

    @ApiModelProperty(value = "하위 카테고리명", example = "정기예금", required = true)
    private String name;

    @ApiModelProperty(value = "하위 카테고리 설명", example = "일정 금액을 정해진 기간 동안 예치하는 상품")
    private String description;
}