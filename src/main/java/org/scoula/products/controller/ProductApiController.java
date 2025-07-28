package org.scoula.products.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.scoula.products.dto.response.FilterOptionsResponse;
import org.scoula.products.dto.response.ProductCompareResponse;
import org.scoula.products.dto.response.ProductDetailResponse;
import org.scoula.products.dto.response.ProductListResponse;
import org.scoula.products.dto.response.deposit.DepositOptionDTO;
import org.scoula.products.dto.response.deposit.DepositProductDTO;
import org.scoula.products.service.ProductCategoryService;
import org.scoula.products.service.ProductCompareService;
import org.scoula.products.service.ProductSearchService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 금융상품 관련 API 컨트롤러
 */
@RestController
@Api(tags = "상품 검색/조회")
@RequestMapping("/api/products")
public class ProductApiController {
    //
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
    @GetMapping
    public ResponseEntity<ApiResponse<ProductListResponse>> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long subCategory,
            @RequestParam(required = false) String banks,

            // 예금 상품 관련 파라미터
            @RequestParam(required = false) String interestRateType,
            @RequestParam(required = false) String saveTerm,
            @RequestParam(required = false) String joinMethod,
            @RequestParam(required = false) Double minIntrRate,

            // 연금 상품 관련 파라미터 추가
            @RequestParam(required = false) String pensionKind,       // 연금 종류(보험,신탁,펀드)
            @RequestParam(required = false) String productType,       // 상품 유형(금리연동형 등)
            @RequestParam(required = false) String riskLevel,         // 위험수준(저위험,중고위험,고위험)
            @RequestParam(required = false) Double minProfitRate,     // 최소 수익률
            @RequestParam(required = false) Double minGuarRate,       // 최저보증이율
            @RequestParam(required = false) Integer entryAge,         // 가입 가능 연령
            @RequestParam(required = false) Integer minPayment,       // 최소 월 납입금액
            @RequestParam(required = false) Integer maxPayment,       // 최대 월 납입금액

            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "desc") String order,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {

        // 필터 파라미터 구성
        Map<String, String> filters = new HashMap<>();

        // 공통 필터
        if (category != null) filters.put("category", category);
        if (subCategory != null) filters.put("subCategory", subCategory.toString());
        if (sort != null) filters.put("sort", sort);
        if (order != null) filters.put("order", order);

        // 카테고리별 필터 처리
        if ("pension".equals(category)) {
            // 연금 상품 전용 필터
            if (pensionKind != null) filters.put("pensionKind", pensionKind);
            if (productType != null) filters.put("productType", productType);
            if (riskLevel != null) filters.put("riskLevel", riskLevel);
            if (minProfitRate != null) filters.put("minProfitRate", minProfitRate.toString());
            if (minGuarRate != null) filters.put("minGuarRate", minGuarRate.toString());
            if (entryAge != null) filters.put("entryAge", entryAge.toString());
            if (minPayment != null) filters.put("minPayment", minPayment.toString());
            if (maxPayment != null) filters.put("maxPayment", maxPayment.toString());
        } else {
            // 예금 상품 등 기존 필터
            if (banks != null) filters.put("banks", banks);
            if (interestRateType != null) filters.put("interestRateType", interestRateType);
            if (saveTerm != null) filters.put("saveTerm", saveTerm);
            if (joinMethod != null) filters.put("joinMethod", joinMethod);
            if (minIntrRate != null) filters.put("minIntrRate", minIntrRate.toString());
        }

        // 서비스 호출
        ProductListResponse response = searchService.searchProducts(keyword, filters, pageNo);

        // ApiResponse 생성
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.PRODUCT_SEARCH_SUCCESS, response));
    }


//    @ApiOperation(value = "상품 자동완성 검색",
//            notes = "키워드 입력 기반 검색어 추천 목록을 제공합니다.")
//    @GetMapping("/autocomplete")
//    public ApiResponse<?> autocompleteProducts(
//            @RequestParam String keyword) {
//        // 기본 응답값 (샘플 데이터)
//        List<String> sampleSuggestions = Arrays.asList(
//                "정기예금",
//                "정기적금",
//                "정기주택청약",
//                "정기연금저축"
//        );
//
//        return ApiResponse.success(ResponseCode.PRODUCT_AUTOCOMPLETE_SUCCESS, sampleSuggestions);
//    }

    @ApiOperation(value = "상품 카테고리 목록",
            notes = "상품 유형별 선택 가능한 카테고리 목록을 제공합니다.")
    @GetMapping("/categories")
    public ApiResponse<?> getCategories() {
        // 기본 버전: 카테고리 목록만 반환
        List<Map<String, Object>> categories = categoryService.getAllCategories();

        // 향상된 버전: 카테고리와 하위 카테고리 정보 함께 반환
        // List<Map<String, Object>> categories = categoryService.getCategoriesWithSubcategories();

        return ApiResponse.success(ResponseCode.PRODUCT_CATEGORY_SUCCESS, categories);
    }

    // 추가 API: 특정 카테고리의 하위 카테고리 조회
    @ApiOperation(value = "카테고리별 하위 카테고리 목록",
            notes = "특정 카테고리에 속한 하위 카테고리 목록을 제공합니다.")
    @GetMapping("/categories/{categoryCode}/subcategories")
    public ApiResponse<?> getSubcategories(@PathVariable String categoryCode) {
        // 카테고리 ID 직접 매핑 (가장 안전한 방법)
        Long categoryId = mapCategoryCodeToId(categoryCode);

        if (categoryId == null) {
            return ApiResponse.fail(ResponseCode.CATEGORY_NOT_FOUND);
        }

        // 카테고리 ID로 하위 카테고리 목록 조회
        List<Map<String, Object>> subcategories = categoryService.getSubcategoriesByCategoryId(categoryId);

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
                    // 숫자로 입력된 경우 직접 ID로 사용
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
        // 서비스 호출하여 필터 옵션 조회
        FilterOptionsResponse filterOptions = searchService.getFilterOptions(category, subCategory);
        return ApiResponse.success(ResponseCode.PRODUCT_FILTER_OPTIONS_SUCCESS, filterOptions);
    }


    @ApiOperation(value = "상품 상세 정보 조회",
            notes = "상품 유형과 코드 기반으로 상품 상세 정보를 제공합니다.")
    @GetMapping("/{productType}/{productId}")
    public ApiResponse<?> getProductDetail(
            @PathVariable String productType,
            @PathVariable Long productId) {

        // 서비스 호출을 통해 상품 상세 정보 조회
        ProductDetailResponse response = searchService.getProductDetail(productType, productId);
        return ApiResponse.success(ResponseCode.PRODUCT_DETAIL_SUCCESS, response);
    }

    private DepositProductDTO createSampleDepositProduct(String productId) {
        DepositProductDTO product = new DepositProductDTO();
        product.setFinPrdtCd(productId);
        product.setFinCoNo("WR001");
        product.setKorCoNm("우리은행");
        product.setFinPrdtNm("WR뱅크 정기예금");
        product.setJoinWay("인터넷뱅킹,스마트폰뱅킹,창구");
        product.setMtrtInt("만기 후 자동 해지");
        product.setSpclCnd("우리은행 급여통장 보유 고객 우대금리 0.5% 적용");
        product.setJoinMember("제한없음");
        product.setJoinDeny("없음");
        product.setJoinAmt(10000000L);
        product.setDclsStrtDay("20230101");

        // 옵션 추가
        List<DepositOptionDTO> options = new ArrayList<>();
        DepositOptionDTO option = new DepositOptionDTO();
        option.setFinPrdtCd(productId);
        option.setSaveTrm(12);
        option.setIntrRateType("S");
        option.setIntrRateTypeNm("단리");
        option.setIntrRate(3.5);
        option.setIntrRate2(4.0);
        options.add(option);

        product.setOptions(options);
        return product;
    }

    @ApiOperation(value = "상품 비교",
            notes = "선택한 상품들의 상세 정보를 비교합니다.")
    @GetMapping("/compare")
    public ApiResponse<?> compareProducts(
            @RequestParam List<String> productIds,
            @RequestParam(required = false, defaultValue = "deposit") String productType,
            @RequestParam(required = false) String optionId) {

        ProductCompareResponse response;

        if ("pension".equals(productType)) {
            // 연금 상품 비교
            response = compareService.comparePensionProducts(productIds, optionId);
        } else {
            // deposit 카테고리 (정기예금, 적금 등 서브카테고리 포함)
            response = compareService.compareProducts(productType, productIds);
        }

        return ApiResponse.success(ResponseCode.PRODUCT_COMPARISON_SUCCESS, response);
    }
}
