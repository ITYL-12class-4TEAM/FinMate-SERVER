package org.scoula.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogoutResponseDTO {
    private String logoutAt;
}