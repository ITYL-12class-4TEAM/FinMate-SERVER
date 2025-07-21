package org.scoula.products.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchRequest {
    // 기존 필드
    private String productType;    // 상품 유형 (deposit, saving, pension)
    private String searchText;     // 검색어
    private Integer page;          // 페이지 번호
    private Integer pageSize;      // 페이지 크기
    private String sortBy;         // 정렬 기준
    private String sortDirection;  // 정렬 방향

    // 금리 필터링
    private Double minIntrRate;    // 최소 금리
    private Double maxIntrRate;    // 최대 금리

    // 확장된 필드
    private Integer saveTrm;       // 저축 기간 (개월)
    private String intrRateType;   // 금리 유형 (S: 단리, M: 복리)
    private String joinWay;        // 가입 방법 (인터넷뱅킹, 스마트폰뱅킹, 창구 등)
    private Boolean isOnline;      // 온라인 가입 여부
    private Long depositAmount;    // 예치 금액
    private String bankName;       // 은행명
}