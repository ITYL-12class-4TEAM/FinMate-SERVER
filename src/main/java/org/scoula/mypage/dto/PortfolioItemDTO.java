package org.scoula.mypage.dto;

import lombok.Data;

@Data
public class PortfolioItemDTO {
    private Long portfolioId;
    private String productName;
    private String categoryName;
    private String subcategoryName;
    private Long amount;
    private String joinDate;
    private String memo;
}

