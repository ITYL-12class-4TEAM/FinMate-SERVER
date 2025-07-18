package org.scoula.wmti.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResult {
    private BigInteger memberId; // 로그인 사용자 ID (nullable 가능)
    private String wmtiCode;
    private BigInteger wmti_id;
    private String answersJson; // 20문항 배열을 JSON 문자열로 저장
    private LocalDateTime submittedAt; //설문검사 시각
}
