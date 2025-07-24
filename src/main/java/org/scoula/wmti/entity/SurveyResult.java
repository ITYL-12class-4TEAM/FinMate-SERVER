package org.scoula.wmti.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.wmti.enums.WMTIDimension;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResult {
    private Long wmtiId;     //PK: wmti결과 고유식별자 As wmti_id
    private Long memberId;   //FK: 로그인 사용자 고유식별자 As member_id
    private String userName;    //사용자 실명 또는 표기이름

    //설문연산인자
    private String answersJson; // 20문항 응답 데이터를 JSON문자열 형태로 저장
    private double aScore; //  A지향성 (%)
    private double pScore; //  P지향성 (%)
    private double mScore; //  M지향성 (%)
    private double lScore; //  L지향성 (%)

    //설문결과인자
    private String wmtiCode; //검사결과 도출된 4자리 wmti코드
    private WMTIDimension A;   // A/I 성향
    private WMTIDimension P;   // P/B 성향
    private WMTIDimension M;   // M/W 성향
    private WMTIDimension L;   // L/C 성향

    private LocalDateTime createdAt; //설문검사 시각
}
