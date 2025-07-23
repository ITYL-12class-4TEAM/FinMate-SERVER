package org.scoula.mypage.dto;

import lombok.Data;

@Data
public class FavoriteRequestDTO {
    private Long memberId;
    private Long productId;
}
