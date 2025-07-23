package org.scoula.wmti.dto.survey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyRequestDTO {
    private List<Integer> answers; // 설문 응답 (1~5까지의 점수 리스트)
    private LocalDateTime startTime; // 설문시작시간
    // 응답 유효성 검증 메서드
    public boolean isValid() {
        // 20개 문항이 모두 응답되어야 합니다.
        return answers != null && answers.size() == 20;
    }
}
