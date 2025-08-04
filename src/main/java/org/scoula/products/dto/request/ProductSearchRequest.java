package org.scoula.products.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 요청 객체를 필터 맵으로 변환
     */
    public Map<String, String> toFilterMap() {
        Map<String, String> filters = new HashMap<>();

        // 카테고리/서브카테고리 정보 추가
        if (this.categoryId != null) {
            filters.put("category", mapCategoryIdToCode(this.categoryId));
        }

        if (this.subCategoryId != null) {
            filters.put("subCategory", this.subCategoryId.toString());
        }

        // 정렬 정보 추가
        if (this.sortBy != null) {
            filters.put("sort", this.sortBy);
        }

        if (this.sortDirection != null) {
            filters.put("order", this.sortDirection);
        }

        // 상품 타입에 따른 필터 처리
        String category = filters.getOrDefault("category",
                this.productType != null ? this.productType : "deposit");

        if ("pension".equals(category)) {
            // 연금 상품 필터 추가
            addPensionFilters(filters);
        } else {
            // 예금/적금 상품 필터 추가
            addDepositFilters(filters);
        }

        return filters;
    }

    /**
     * 연금 상품 필터 추가
     */
    private void addPensionFilters(Map<String, String> filters) {
        // 최소 금리/수익률
        if (this.minIntrRate != null) {
            filters.put("minProfitRate", this.minIntrRate.toString());
        }

        // 온라인 가입 여부
        if (this.isOnline != null) {
            filters.put("isOnline", this.isOnline.toString());
        }
    }

    /**
     * 예금/적금 상품 필터 추가
     */
    private void addDepositFilters(Map<String, String> filters) {
        // 은행 목록
        if (this.banks != null && !this.banks.isEmpty()) {
            filters.put("banks", String.join(",", this.banks));
        } else if (this.bankStr != null && !this.bankStr.isEmpty()) {
            filters.put("banks", this.bankStr);
        }

        // 금리 유형
        if (this.intrRateType != null) {
            filters.put("interestRateType", this.intrRateType);
        }

        // 저축 기간
        if (this.saveTrm != null) {
            filters.put("saveTerm", this.saveTrm.toString());
        }

        // 가입 방법
        if (this.joinWay != null) {
            filters.put("joinMethod", this.joinWay);
        }

        // 최소 금리
        if (this.minIntrRate != null) {
            filters.put("minIntrRate", this.minIntrRate.toString());
        }

        // 온라인 가입 여부
        if (this.isOnline != null) {
            filters.put("isOnline", this.isOnline.toString());
        }

        // 예치 금액 또는 월 납입 금액 (서브카테고리에 따라 다름)
        if (this.depositAmount != null) {
            filters.put("amount", this.depositAmount.toString());
        }

        if (this.monthlyPayment != null) {
            filters.put("amount", this.monthlyPayment.toString());
        }
    }

    /**
     * 카테고리 ID를 코드로 변환
     */
    private String mapCategoryIdToCode(Long categoryId) {
        if (categoryId == null) return "deposit"; // 기본값

        switch (categoryId.intValue()) {
            case 1:
                return "deposit";    // 예금
            case 2:
                return "loan";       // 대출
            case 3:
                return "fund";       // 펀드
            case 4:
                return "insurance";  // 보험
            case 5:
                return "pension";    // 연금
            case 6:
                return "realestate"; // 부동산
            default:
                return "deposit";   // 기본값
        }
    }
}