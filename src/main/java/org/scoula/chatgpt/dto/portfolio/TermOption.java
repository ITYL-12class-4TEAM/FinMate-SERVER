package org.scoula.chatgpt.dto.portfolio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TermOption {
    private Integer months;        // 12, 24, 36
    private Double interestRate;   // 해당 기간의 금리
    private String description;    // "1년", "2년", "3년"
}