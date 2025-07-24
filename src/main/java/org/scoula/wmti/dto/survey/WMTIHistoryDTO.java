package org.scoula.wmti.dto.survey;

import lombok.Data;
import org.scoula.wmti.enums.RiskPreference;
import org.scoula.wmti.enums.WMTIDimension;

import java.time.LocalDateTime;

@Data
public class WMTIHistoryDTO {
    private Long historyId;    // 이력 고유 ID
    private String wmtiCode;   // 도출된 4자리 WMTI 코드
    private String answersJson; // 설문 응답 데이터 (JSON 형태)
    private String resultType;  // 설문 유형 (필요시 추가)

    private WMTIDimension A;   // A/I 성향
    private WMTIDimension P;   // P/B 성향
    private WMTIDimension M;   // M/W 성향
    private WMTIDimension L;   // L/C 성향

    private RiskPreference riskPreference; // 파생된 위험 성향
    private LocalDateTime createdAt; // 설문 저장일
}
