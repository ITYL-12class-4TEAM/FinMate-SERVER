package org.scoula.wmti.dto.survey;

import lombok.Data;

import java.util.List;

@Data
public class WMTIProfileDTO {
    private String aka;
    private List<String> tag;
    private String description;
}
