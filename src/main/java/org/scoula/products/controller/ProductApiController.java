package org.scoula.products.controller;

import org.scoula.products.dto.response.ProductListResponse;
import org.scoula.products.service.ProductCompareService;
import org.scoula.products.service.ProductSearchService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 금융상품 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/products")
public class ProductApiController {
    //
    private final ProductSearchService searchService;
    private final ProductCompareService compareService;

    @Autowired
    public ProductApiController(
            ProductSearchService searchService,
            ProductCompareService compareService) {
        this.searchService = searchService;
        this.compareService = compareService;
    }

    /**
     * 상품 목록 조회
     * 초기 진입 시 기본 상품 리스트 조회 및 다양한 조건으로 필터링할 수 있는 API
     */
    @GetMapping
    public ResponseEntity<ApiResponse<ProductListResponse>> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String interestRateType,
            @RequestParam(required = false) String saveTerm,
            @RequestParam(required = false) String joinMethod,
            @RequestParam(defaultValue = "1") int pageNo) {
        // 필터 파라미터 구성
        Map<String, String> filters = new HashMap<>();
        if (category != null) filters.put("category", category);
        if (interestRateType != null) filters.put("interestRateType", interestRateType);
        if (saveTerm != null) filters.put("saveTerm", saveTerm.toString());
        if (joinMethod != null) filters.put("joinMethod", joinMethod);

        // 서비스 호출
        ProductListResponse response = searchService.searchProducts(keyword, filters, pageNo);

        // ApiResponse 생성
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.PRODUCT_SEARCH_SUCCESS, response));

    }
}
