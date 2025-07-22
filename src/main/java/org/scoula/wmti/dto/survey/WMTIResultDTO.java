package org.scoula.wmti.dto.survey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WMTIResultDTO {
    private String userName;    //사용자 이름
    private String wmtiCode;    //연산도출결과 코드4자리
    private String summary;     //간단한 분석결과 (예:"타고난 리더형 투자지도자"
}
