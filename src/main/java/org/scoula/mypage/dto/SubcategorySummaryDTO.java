package org.scoula.mypage.dto;

import lombok.Data;

@Data
public class SubcategorySummaryDTO {
    private String subcategoryName;
    private Long totalAmount;
    private Double ratio;
}
