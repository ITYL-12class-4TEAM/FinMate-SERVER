package org.scoula.wmti.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.wmti.enums.RiskPreference;
import org.scoula.wmti.enums.WMTIDimension;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WMTIHistory {
    private Long historyId;     //PK 설문이력 고유식별자
    private Long memberId;      //FK 사용자 고유 식별자
    private String resultType;  //Fk 사전정보입력 결과 도출된 투자자유형
    private RiskPreference riskPreference; // FK: 사전정보입력 결과 도출된 위험 성향 ENUM(’안정형’, ‘안정추구형’, ‘위험중립형’, ‘적극투자형’, ‘공격투자형’)
    private String userName;

    //설문연산인자
    private String answersJson; // 설문 응답 데이터 (JSON 형태)
    private double aScore; //  A지향성 (%)
    private double iScore; //  I지향성 (%)
    private double pScore; //  P지향성 (%)
    private double bScore; //  B지향성 (%)
    private double mScore; //  M지향성 (%)
    private double wScore; //  W지향성 (%)
    private double lScore; //  L지향성 (%)
    private double cScore; //  C지향성 (%)

    //설문결과인자
    private String wmtiCode;
    private WMTIDimension a;             // A/I 성향
    private WMTIDimension p;             // P/B 성향
    private WMTIDimension m;             // M/W 성향
    private WMTIDimension l;             // L/C 성향

    private LocalDateTime createdAt; // 설문 저장일
}
