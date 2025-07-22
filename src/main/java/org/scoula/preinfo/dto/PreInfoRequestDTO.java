package org.scoula.preinfo.dto;

import lombok.Data;

@Data
public class PreInfoRequestDTO {
    private String username;
    private Integer age;
    private Long income;
    private Long fixedCost;
    private String period;
    private String purpose;
}
