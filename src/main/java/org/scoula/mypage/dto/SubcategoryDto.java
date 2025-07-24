package org.scoula.mypage.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "서브카테고리 정보 DTO")
public class SubcategoryDto {

    @ApiModelProperty(value = "서브카테고리 ID", example = "10")
    private Long subcategoryId;

    @ApiModelProperty(value = "서브카테고리 이름", example = "정기예금")
    private String subcategoryName;
}
