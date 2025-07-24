package org.scoula.mypage.dto;

import lombok.Data;

@Data
public class PortfolioCreateDTO {
    private Long productId;
    private Long categoryId;
    private Long subcategoryId;
    private Long amount;
    private String joinDate;
    private String memo;
}
