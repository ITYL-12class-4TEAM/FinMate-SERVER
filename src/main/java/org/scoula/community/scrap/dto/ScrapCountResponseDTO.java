package org.scoula.community.scrap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScrapCountResponseDTO {
    private Long postId;
    private int scrapCount;

    public static ScrapCountResponseDTO of(Long postId, int scrapCount) {
        return ScrapCountResponseDTO.builder()
                .postId(postId)
                .scrapCount(scrapCount)
                .build();
    }
}