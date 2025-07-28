package org.scoula.community.scrap.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostScrapVO {
    private Long scrapId;
    private Long postId;
    private Long memberId;
    private LocalDateTime scrapedAt;
}
