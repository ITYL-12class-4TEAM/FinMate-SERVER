package org.scoula.products.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.scoula.products.dto.request.ProductSearchRequest;
import org.scoula.products.dto.response.*;
import org.scoula.products.service.ProductCategoryService;
import org.scoula.products.service.ProductCompareService;
import org.scoula.products.service.ProductSearchService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @ApiOperation(value = "상품 목록 조회", notes = "초기 진입 시 기본 상품 리스트 조회 및 다양한 조건으로 필터링할 수 있는 API")
    @PostMapping("/search")
    public ApiResponse<ProductListResponse> searchProducts(@RequestBody @Valid ProductSearchRequest request) {
        Map<String, String> filters = request.toFilterMap();
        ProductListResponse response = searchService.searchProducts(
                request.getSearchText(),
                filters,
                request.getPage() != null ? request.getPage() : 1);
        return ApiResponse.success(ResponseCode.PRODUCT_SEARCH_SUCCESS, response);
    }

    @ApiOperation(value = "상품 카테고리 목록", notes = "상품 유형별 선택 가능한 전체 카테고리 목록을 제공합니다.")
    @GetMapping("/categories")
    public ApiResponse<List<CategoryDTO>> getCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ApiResponse.success(ResponseCode.PRODUCT_CATEGORY_SUCCESS, categories);
    }

    @ApiOperation(value = "카테고리별 하위 카테고리 목록", notes = "특정 카테고리에 속한 하위 카테고리 목록을 제공합니다.")
    @GetMapping("/categories/{categoryCode}/subcategories")
    public ApiResponse<List<SubcategoryDTO>> getSubcategories(
            @ApiParam(value = "카테고리 코드 (e.g., 'deposit', 'loan', 'pension')", required = true, example = "deposit")
            @PathVariable String categoryCode) {

        Long categoryId = mapCategoryCodeToId(categoryCode);

        if (categoryId == null) {
            return ApiResponse.fail(ResponseCode.CATEGORY_NOT_FOUND);
        }

        List<SubcategoryDTO> subcategories = categoryService.getSubcategoriesByCategoryId(categoryId);
        return ApiResponse.success(ResponseCode.SUBCATEGORY_SUCCESS, subcategories);
    }

    @ApiOperation(value = "필터 옵션 조회", notes = "카테고리별 사용 가능한 필터 옵션을 제공합니다. (은행 목록, 가입 방법 등)")
    @GetMapping("/filter-options")
    public ApiResponse<FilterOptionsResponse> getFilterOptions(
            @ApiParam(value = "상품 카테고리 코드", required = true, defaultValue = "deposit", example = "deposit")
            @RequestParam(required = false, defaultValue = "deposit") String category,

            @ApiParam(value = "하위 카테고리 ID (숫자)", required = false, example = "101")
            @RequestParam(value = "subCategory", required = false) Long subCategory) {

        FilterOptionsResponse filterOptions = searchService.getFilterOptions(category, subCategory);
        return ApiResponse.success(ResponseCode.PRODUCT_FILTER_OPTIONS_SUCCESS, filterOptions);
    }

    @ApiOperation(value = "상품 상세 정보 조회", notes = "상품 유형과 상품 ID를 기반으로 단일 상품의 상세 정보를 제공합니다.")
    @GetMapping("/{productType}/{productId}")
    public ApiResponse<ProductDetailResponse> getProductDetail(
            @ApiParam(value = "상품 유형", required = true, example = "deposit")
            @PathVariable String productType,

            @ApiParam(value = "상품 ID (숫자)", required = true, example = "1")
            @PathVariable Long productId) {

        System.out.println("------------------");
        ProductDetailResponse response = searchService.getProductDetail(productType, productId);
        return ApiResponse.success(ResponseCode.PRODUCT_DETAIL_SUCCESS, response);
    }

    @ApiOperation(value = "상품 비교", notes = "선택한 여러 상품들의 특정 옵션 정보를 한눈에 비교합니다.")
    @GetMapping("/compare")
    public ApiResponse<ProductCompareResponse> compareProducts(
            @ApiParam(value = "비교할 상품 ID 목록", required = true, example = "1,5,12")
            @RequestParam List<String> productIds,

            @ApiParam(value = "상품 유형", defaultValue = "deposit", example = "deposit")
            @RequestParam(required = false, defaultValue = "deposit") String productType,

            @ApiParam(value = "가입 기간 옵션 목록 (상품 순서에 맞게 전달)", required = false, example = "6,12,24")
            @RequestParam(required = false) List<String> saveTrm,

            @ApiParam(value = "금리 유형 옵션 목록 (S:단리, M:복리)", required = false, example = "S,S,M")
            @RequestParam(required = false) List<String> intrRateType,

            @ApiParam(value = "적립 방식 옵션 목록 (S:정기, F:자유)", required = false, example = "F,F,S")
            @RequestParam(required = false) List<String> rsrvType) {

        Map<String, Map<String, String>> productOptions = new HashMap<>();
        for (int i = 0; i < productIds.size(); i++) {
            Map<String, String> options = new HashMap<>();
            if (saveTrm != null && i < saveTrm.size()) {
                options.put("saveTrm", saveTrm.get(i));
            }
            if (intrRateType != null && i < intrRateType.size()) {
                options.put("intrRateType", intrRateType.get(i));
            }
            if (rsrvType != null && i < rsrvType.size()) {
                options.put("rsrvType", rsrvType.get(i));
            }
            productOptions.put(productIds.get(i), options);
        }

        ProductCompareResponse response = compareService.compareProductsWithOptions(productType, productOptions);
        return ApiResponse.success(ResponseCode.PRODUCT_COMPARISON_SUCCESS, response);
    }

    // 영문 코드를 카테고리 ID로 직접 매핑하는 메소드
    private Long mapCategoryCodeToId(String categoryCode) {
        switch (categoryCode.toLowerCase()) {
            case "deposit":
                return 1L;
            case "loan":
                return 2L;
            case "fund":
                return 3L;
            case "insurance":
                return 4L;
            case "pension":
                return 5L;
            case "realestate":
                return 6L;
            default:
                try {
                    return Long.valueOf(categoryCode);
                } catch (NumberFormatException e) {
                    return null;
                }
        }
    }
}