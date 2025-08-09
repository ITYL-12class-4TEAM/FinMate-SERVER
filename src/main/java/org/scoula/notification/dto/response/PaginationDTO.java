package org.scoula.notification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationDTO {
    private int currentPage;
    private int totalPages;
    private long totalCount;
    private long unreadCount;
}