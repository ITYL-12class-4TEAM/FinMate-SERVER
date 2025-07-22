package org.scoula.userprofile.dto;

import lombok.Data;

@Data
public class UserProfileRequestDTO {
    private String username;
    private Integer age;
    private Long income;
    private Long fixedCost;
    private String period;
    private String purpose;
}
