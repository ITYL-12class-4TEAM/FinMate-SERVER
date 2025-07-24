package org.scoula.products.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchRequest {
    // productType은 유지하되 점진적으로 사용 중단 예정
    private String productType;    // 상품 유형 (향후 제거 예정)

    // 카테고리/서브카테고리 기반 필터링
    private Long categoryId;       // 카테고리 ID (1: 예금, 2: 대출, 등)
    private Long subCategoryId;    // 서브카테고리 ID (101: 정기예금, 102: 자유적금, 등)

    // 기존 필드들...
    private String searchText;     // 검색어
    private Integer page;          // 페이지 번호
    private Integer pageSize;      // 페이지 크기
    private String sortBy;         // 정렬 기준
    private String sortDirection;  // 정렬 방향
    private Double minIntrRate;    // 최소 금리
    private Double maxIntrRate;    // 최대 금리
    private Integer saveTrm;       // 저축 기간 (개월)
    private String intrRateType;   // 금리 유형 (S: 단리, M: 복리)
    private String joinWay;        // 가입 방법
    private Boolean isOnline;      // 온라인 가입 여부

    // 금액 필터링 - 서브카테고리별 다른 의미
    private Long depositAmount;    // 예치 금액 (서브카테고리 101: 정기예금용)
    private Long monthlyPayment;   // 월 납입 금액 (서브카테고리 102: 자유적금용)

    private List<String> banks;    // 은행명 목록
    private String bankStr;        // 은행명 문자열
}