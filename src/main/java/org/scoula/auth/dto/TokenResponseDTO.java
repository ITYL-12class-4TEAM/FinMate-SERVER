package org.scoula.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponseDTO {
    private boolean success;
    private String message;
    private DataDTO data;

    @Data
    @AllArgsConstructor
    public static class DataDTO {
        private String accessToken;
    }
}