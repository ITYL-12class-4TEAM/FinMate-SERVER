package org.scoula.products.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import org.scoula.products.dto.request.ProductSearchRequest;
import org.scoula.products.dto.response.*;
import org.scoula.products.service.ProductCategoryService;
import org.scoula.products.service.ProductCompareService;
import org.scoula.products.service.ProductSearchService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 금융상품 관련 API 컨트롤러
 */
@RestController
@Api(tags = "상품 검색/조회")
@RequestMapping("/api/products")
public class ProductApiController {

    private final ProductSearchService searchService;
    private final ProductCompareService compareService;
    private final ProductCategoryService categoryService;

    @Autowired
    public ProductApiController(
            ProductSearchService searchService,
            ProductCompareService compareService,
            ProductCategoryService categoryService) {
        this.searchService = searchService;
        this.compareService = compareService;
        this.categoryService = categoryService;
    }

    /**
     * 상품 목록 조회
     */
    @ApiOperation(value = "상품 목록 조회",
            notes = "초기 진입 시 기본 상품 리스트 조회 및 다양한 조건으로 필터링할 수 있는 API")
    @PostMapping("/search")
    public ApiResponse<ProductListResponse> searchProducts(@RequestBody @Valid ProductSearchRequest request) {
        // 필터 맵 생성
        Map<String, String> filters = request.toFilterMap();

        // 서비스 호출
        ProductListResponse response = searchService.searchProducts(
                request.getSearchText(),
                filters,
                request.getPage() != null ? request.getPage() : 1);

        // ApiResponse 생성
        return ApiResponse.success(ResponseCode.PRODUCT_SEARCH_SUCCESS, response);
    }

    @ApiOperation(value = "상품 카테고리 목록",
            notes = "상품 유형별 선택 가능한 카테고리 목록을 제공합니다.")
    @GetMapping("/categories")
    public ApiResponse<?> getCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ApiResponse.success(ResponseCode.PRODUCT_CATEGORY_SUCCESS, categories);
    }

    @ApiOperation(value = "카테고리별 하위 카테고리 목록",
            notes = "특정 카테고리에 속한 하위 카테고리 목록을 제공합니다.")
    @GetMapping("/categories/{categoryCode}/subcategories")
    public ApiResponse<?> getSubcategories(@PathVariable String categoryCode) {
        Long categoryId = mapCategoryCodeToId(categoryCode);

        if (categoryId == null) {
            return ApiResponse.fail(ResponseCode.CATEGORY_NOT_FOUND);
        }

        List<SubcategoryDTO> subcategories = categoryService.getSubcategoriesByCategoryId(categoryId);
        return ApiResponse.success(ResponseCode.SUBCATEGORY_SUCCESS, subcategories);
    }

    // 영문 코드를 카테고리 ID로 직접 매핑하는 메소드
    private Long mapCategoryCodeToId(String categoryCode) {
        switch (categoryCode.toLowerCase()) {
            case "deposit":
                return 1L;  // 예금 카테고리 ID
            case "loan":
                return 2L;  // 대출 카테고리 ID
            case "fund":
                return 3L;  // 펀드 카테고리 ID
            case "insurance":
                return 4L;  // 보험 카테고리 ID
            case "pension":
                return 5L;  // 연금 카테고리 ID
            case "realestate":
                return 6L;  // 부동산 카테고리 ID
            default:
                try {
                    return Long.valueOf(categoryCode);
                } catch (NumberFormatException e) {
                    return null;  // 매핑 실패
                }
        }
    }

    @ApiOperation(value = "필터 옵션 조회",
            notes = "카테고리별 사용 가능한 필터 옵션을 제공합니다.")
    @GetMapping("/filter-options")
    public ApiResponse<?> getFilterOptions(
            @RequestParam(required = false, defaultValue = "deposit") String category,
            @RequestParam(value = "subCategory", required = false) Long subCategory
    ) {
        FilterOptionsResponse filterOptions = searchService.getFilterOptions(category, subCategory);
        return ApiResponse.success(ResponseCode.PRODUCT_FILTER_OPTIONS_SUCCESS, filterOptions);
    }

    @ApiOperation(value = "상품 상세 정보 조회",
            notes = "상품 유형과 코드 기반으로 상품 상세 정보를 제공합니다.")
    @GetMapping("/{productType}/{productId}")
    public ApiResponse<?> getProductDetail(
            @PathVariable String productType,
            @PathVariable Long productId) {
        ProductDetailResponse response = searchService.getProductDetail(productType, productId);
        return ApiResponse.success(ResponseCode.PRODUCT_DETAIL_SUCCESS, response);
    }

    @ApiOperation(value = "상품 비교",
            notes = "선택한 상품들의 특정 옵션 정보를 비교합니다.")
    @GetMapping("/compare")
    public ApiResponse<?> compareProducts(
            @RequestParam List<String> productIds,
            @RequestParam(required = false, defaultValue = "deposit") String productType,
            @RequestParam(required = false) List<String> saveTrm,          // 가입 기간 (예: 6,12,24)
            @RequestParam(required = false) List<String> intrRateType) {   // 금리 유형 (예: S,M)

        // 상품 ID와 옵션 정보를 매핑
        Map<String, Map<String, String>> productOptions = new HashMap<>();

        // productIds 길이만큼 옵션 정보가 있다고 가정
        for (int i = 0; i < productIds.size(); i++) {
            Map<String, String> options = new HashMap<>();

            // 가입 기간 정보가 있으면 추가
            if (saveTrm != null && i < saveTrm.size()) {
                options.put("saveTrm", saveTrm.get(i));
            }

            // 금리 유형 정보가 있으면 추가
            if (intrRateType != null && i < intrRateType.size()) {
                options.put("intrRateType", intrRateType.get(i));
            }

            productOptions.put(productIds.get(i), options);
        }

        ProductCompareResponse response = compareService.compareProductsWithOptions(
                productType, productOptions);

        return ApiResponse.success(ResponseCode.PRODUCT_COMPARISON_SUCCESS, response);
    }
}