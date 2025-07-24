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
    private Long historyId; //PK 설문이력 고유식별자
    private Long memberId;  //FK 사용자 고유 식별자
    private String answersJson; // 설문 응답 데이터 (JSON 형태)
    private String resultType;
    private String wmtiCode;

    private WMTIDimension A;             // A/I 성향
    private WMTIDimension P;             // P/B 성향
    private WMTIDimension M;             // M/W 성향
    private WMTIDimension L;             // L/C 성향

    private LocalDateTime createdAt; // 설문 저장일
    private RiskPreference riskPreference; // 파생된 위험 성향 ENUM(’안정형’, ‘안정추구형’, ‘위험중립형’, ‘적극투자형’, ‘공격투자형’)
}
