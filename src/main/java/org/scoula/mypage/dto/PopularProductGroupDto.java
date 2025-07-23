package org.scoula.mypage.dto;

import lombok.Data;
import java.util.List;

@Data
public class PopularProductGroupDto {
    private Long subcategoryId;
    private String subcategoryName;
    private List<FavoriteProductDto> products;
}
