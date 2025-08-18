package org.scoula.community.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDTO<T> {
    private List<T> content;        // 현재 페이지의 데이터
    private int currentPage;        // 현재 페이지 번호
    private int size;               // 페이지 크기
    private long totalElements;     // 전체 데이터 개수
    private int totalPages;         // 전체 페이지 수
    private boolean first;          // 첫 번째 페이지 여부
    private boolean last;           // 마지막 페이지 여부
    private boolean hasNext;        // 다음 페이지 존재 여부
    private boolean hasPrevious;    // 이전 페이지 존재 여부

    public static <T> PageResponseDTO<T> of(List<T> content, PageRequestDTO pageRequest, long totalElements) {
        PageResponseDTO<T> response = new PageResponseDTO<>();

        response.content = content;
        response.currentPage = pageRequest.getPage();
        response.size = pageRequest.getSize();
        response.totalElements = totalElements;
        response.totalPages = (int) Math.ceil((double) totalElements / pageRequest.getSize());
        response.first = pageRequest.getPage() == 1;
        response.last = pageRequest.getPage() >= response.totalPages;
        response.hasNext = pageRequest.getPage() < response.totalPages;
        response.hasPrevious = pageRequest.getPage() > 1;

        return response;
    }
}