package org.scoula.mypage.dto;

import lombok.Data;


@Data
public class FavoriteProductDto {
    private Long productId;         // 상품 ID
    private String productName;     // 상품 이름
    private String korCoNm;         // 금융회사 이름
    private String riskLevel;       // 위험도 (LOW, MEDIUM, HIGH)
    private String externalLink;    // 상품 외부 링크
    private Integer wishlistCount;  // 찜한 사람 수
}

