package org.scoula.community.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDTO {
    private int page = 1;        // 현재 페이지 번호 (1부터 시작)
    private int size = 5;        // 페이지당 항목 수 (기본값 5개)

    // MyBatis에서 사용할 offset 계산
    public int getOffset() {
        return (page - 1) * size;
    }

    // 페이지 번호가 1보다 작으면 1로 설정
    public void setPage(int page) {
        this.page = Math.max(page, 1);
    }

    // 페이지 크기가 1보다 작거나 100보다 크면 기본값으로 설정
    public void setSize(int size) {
        if (size < 1 || size > 100) {
            this.size = 5;
        } else {
            this.size = size;
        }
    }
}