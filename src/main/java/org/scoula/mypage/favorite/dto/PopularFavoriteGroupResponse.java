package org.scoula.mypage.favorite.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "카테고리별 인기 관심상품 그룹 DTO")
public class PopularFavoriteGroupResponse {

    @ApiModelProperty(value = "서브카테고리 ID", example = "2001")
    private Long subcategoryId;

    @ApiModelProperty(value = "서브카테고리 이름", example = "정기예금")
    private String subcategoryName;

    @ApiModelProperty(
            value = "해당 카테고리의 인기 상품 목록",
            example = "[{ \"productId\": 101, \"productName\": \"국민 정기예금\" }]"
    )
    private List<FavoriteProductResponse> products;
}
