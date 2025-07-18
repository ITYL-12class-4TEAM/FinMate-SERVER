package org.scoula.products.service.impl;

import org.scoula.products.dto.response.ProductDetailResponse;
import org.scoula.products.dto.response.ProductListResponse;
import org.scoula.products.exception.ProductNotFoundException;
import org.scoula.products.service.ProductSearchService;
import org.scoula.response.ResponseCode;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 상품 검색 서비스 구현체
 * 현재는 Mock 데이터를 반환하지만, 추후 실제 API 연동 코드로 대체될 예정입니다.
 */
@Service
public class ProductSearchServiceImpl implements ProductSearchService {

    /**
     * 상품 목록 조회
     */
    @Override
    public ProductListResponse searchProducts(String keyword, Map<String, String> filters, int pageNo) {
        // Mock 데이터 생성
        ProductListResponse response = new ProductListResponse();
        List<ProductListResponse.ProductSummary> products = new ArrayList<>();

        // 샘플 데이터 10개 생성
        for (int i = 1; i <= 10; i++) {
            ProductListResponse.ProductSummary product = new ProductListResponse.ProductSummary();
            product.setProductId("PROD" + i);
            product.setCompanyId("COMP" + (i % 3 + 1));
            product.setProductName("샘플 정기예금 " + i);
            product.setCompanyName("샘플은행 " + (i % 3 + 1));
            product.setInterestRate(1.5 + (i % 5) * 0.1);
            product.setSpecialRate(2.0 + (i % 5) * 0.2);
            product.setSaveTerm(12);
            product.setJoinMethod(i % 2 == 0 ? "온라인" : "방문");

            products.add(product);
        }

        response.setProducts(products);
        response.setTotalCount(100);
        response.setPageNo(pageNo);
        response.setTotalPages(10);

        return response;
    }

    /**
     * 상품 자동완성 검색
     */
    @Override
    public List<String> autocompleteProducts(String keyword) {
        // Mock 자동완성 데이터
        List<String> suggestions = new ArrayList<>();
        suggestions.add(keyword + " 정기예금");
        suggestions.add(keyword + " 적금");
        suggestions.add(keyword + " 연금저축");
        suggestions.add(keyword + " 우대금리");
        suggestions.add(keyword + " 은행");

        return suggestions;
    }

    /**
     * 상품 카테고리 목록 조회
     */
    @Override
    public List<Map<String, Object>> getCategories() {
        // Mock 카테고리 데이터
        List<Map<String, Object>> categories = new ArrayList<>();

        Map<String, Object> category1 = new HashMap<>();
        category1.put("id", "deposit");
        category1.put("name", "정기예금");
        categories.add(category1);

        Map<String, Object> category2 = new HashMap<>();
        category2.put("id", "saving");
        category2.put("name", "적금");
        categories.add(category2);

        Map<String, Object> category3 = new HashMap<>();
        category3.put("id", "pension");
        category3.put("name", "연금저축");
        categories.add(category3);

        return categories;
    }

    /**
     * 필터 옵션 조회
     */
    @Override
    public Map<String, Object> getFilterOptions() {
        // Mock 필터 옵션 데이터
        Map<String, Object> filterOptions = new HashMap<>();

        // 금리 유형 옵션
        List<Map<String, Object>> interestTypes = new ArrayList<>();
        Map<String, Object> type1 = new HashMap<>();
        type1.put("value", "simple");
        type1.put("label", "단리");
        interestTypes.add(type1);

        Map<String, Object> type2 = new HashMap<>();
        type2.put("value", "compound");
        type2.put("label", "복리");
        interestTypes.add(type2);

        filterOptions.put("interestTypes", interestTypes);

        // 저축 기간 옵션
        List<Map<String, Object>> saveTerms = new ArrayList<>();
        for (int term : new int[]{1, 3, 6, 12, 24, 36}) {
            Map<String, Object> termOption = new HashMap<>();
            termOption.put("value", term);
            termOption.put("label", term + "개월");
            saveTerms.add(termOption);
        }

        filterOptions.put("saveTerms", saveTerms);

        // 가입 방식 옵션
        List<Map<String, Object>> joinMethods = new ArrayList<>();
        Map<String, Object> method1 = new HashMap<>();
        method1.put("value", "online");
        method1.put("label", "온라인");
        joinMethods.add(method1);

        Map<String, Object> method2 = new HashMap<>();
        method2.put("value", "offline");
        method2.put("label", "방문");
        joinMethods.add(method2);

        filterOptions.put("joinMethods", joinMethods);

        return filterOptions;
    }

    /**
     * 상품 상세 정보 조회
     */
    @Override
    public ProductDetailResponse getProductDetail(String productId) {
        // productId 검증
        if (productId == null || productId.isEmpty()) {
            throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
        }

        // Mock 상품 상세 정보
        ProductDetailResponse detail = new ProductDetailResponse();
        detail.setProductId(productId);
        detail.setCompanyId("COMP1");
        detail.setProductName("샘플 정기예금 상세");
        detail.setCompanyName("샘플은행");
        detail.setProductType("정기예금");
        detail.setJoinMethod("온라인/방문");
        detail.setSpecialCondition("급여 이체 및 카드 실적 시 우대금리 적용");
        detail.setMaturityInterest("만기 후 1.0% 적용");

        // 금리 옵션
        List<ProductDetailResponse.InterestOption> options = new ArrayList<>();
        for (int term : new int[]{12, 24, 36}) {
            ProductDetailResponse.InterestOption option = new ProductDetailResponse.InterestOption();
            option.setInterestRateType("단리");
            option.setSaveTerm(term);
            option.setInterestRate(1.5 + (term / 12) * 0.1);
            option.setSpecialRate(2.0 + (term / 12) * 0.2);
            options.add(option);
        }

        detail.setInterestOptions(options);

        return detail;
    }
}