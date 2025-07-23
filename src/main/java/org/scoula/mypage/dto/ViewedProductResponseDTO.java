package org.scoula.mypage.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViewedProductResponseDTO {
    private Long productId;
    private String productName;
    private String korCoNm;
    private String riskLevel;
    private String externalLink;
    private Integer wishlistCount;
    private String viewedAt;  // 👈 이 필드를 추가하면 진짜 Response 전용 DTO
}
