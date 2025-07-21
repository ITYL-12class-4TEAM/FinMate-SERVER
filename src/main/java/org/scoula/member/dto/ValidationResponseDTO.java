package org.scoula.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationResponseDTO {
    private boolean success;
    private String message;
    private DataDTO data;

    @Data
    @AllArgsConstructor
    public static class DataDTO {
        private boolean available;
        private String type;
    }
}