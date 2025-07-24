package org.scoula.mypage.dto;

import lombok.Data;

import java.util.List;

@Data
public class PortfolioSummaryDTO {
    private String categoryName;
    private Long totalAmount;
    private Double ratio;
    private List<SubcategorySummaryDTO> subcategories;
}

