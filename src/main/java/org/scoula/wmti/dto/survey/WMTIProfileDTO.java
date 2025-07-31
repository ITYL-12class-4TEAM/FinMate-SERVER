package org.scoula.wmti.dto.survey;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class WMTIProfileDTO {
    @ApiModelProperty(value = "식별 ID", example = "1")
    private Integer id;

    @ApiModelProperty(value = "WMTI 성향 코드", example = "APML")
    private String code;

    @ApiModelProperty(value = "성향 코드 별 별칭", example = "타고난 리더형 투자 지도자")
    private String aka;

    @ApiModelProperty(value = "성향을 요약하는 태그 목록")
    private List<String> tag;

    @ApiModelProperty(value = "WMTI 성향 코드 설명 상세")
    private String description;
}
