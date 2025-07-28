package org.scoula.community.scrap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScrapResponseDTO {
    private Long postId;
    private boolean isScraped;
    private int scrapCount;

    public static ScrapResponseDTO of(Long postId, boolean isScraped, int scrapCount) {
        return ScrapResponseDTO.builder()
                .postId(postId)
                .isScraped(isScraped)
                .scrapCount(scrapCount)
                .build();
    }
}
