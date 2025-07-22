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
    private Long wmtiId;     //PK: wmti결과 고유식별자 As wmti_id
    private Long memberId;   //FK: 로그인 사용자 고유식별자 As member_id
    private String wmtiCode; //검사결과 도출된 4자리 wmti코드
    private String userName;    //사용자 실명 또는 표기이름

    private String answersJson; // 20문항 배열을 JSON 문자열로 저장

    private LocalDateTime submittedAt; //설문검사 시각
}
