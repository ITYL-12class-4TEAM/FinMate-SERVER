package org.scoula.wmti.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyRequestDTO {
    private Long wmtiId;       // WMTI 결과 고유 ID
    private Long memberId;     // 사용자 고유 ID
    private String wmtiCode;   // 도출된 4자리 WMTI 코드
    private String answersJson; // 설문 응답 데이터 JSON 형태로 저장 (20개문항, 각 1점~5점)
    private LocalDateTime createdAt; // 설문 제출 시간
}
