package org.scoula.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FindIdResponseDTO {
    private boolean success;
    private String message;
    private String email;
}