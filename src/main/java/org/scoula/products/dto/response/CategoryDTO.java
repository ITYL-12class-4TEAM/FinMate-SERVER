package org.scoula.products.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "상품 카테고리 정보 DTO")
public class CategoryDTO {

    @ApiModelProperty(value = "카테고리 ID", example = "1", required = true)
    private Long categoryId;

    @ApiModelProperty(value = "카테고리명", example = "예금", required = true)
    private String name;

    @ApiModelProperty(value = "카테고리 설명", example = "정기예금, 자유적금 등 예금 관련 상품")
    private String description;

    @ApiModelProperty(value = "하위 카테고리 목록")
    private List<SubcategoryDTO> subcategories;
}