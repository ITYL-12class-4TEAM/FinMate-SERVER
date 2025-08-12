package org.scoula.chatgpt.dto.portfolio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateOption {
    private String condition;      // "기본금리", "급여이체 우대", "청년 우대"
    private Double rate;          // 해당 조건의 금리
    private String description;   // 상세 설명
}