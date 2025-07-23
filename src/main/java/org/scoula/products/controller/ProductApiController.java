package org.scoula.products.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
     * 초기 진입 시 기본 상품 리스트 조회 및 다양한 조건으로 필터링할 수 있는 API
     */
    @ApiOperation(value = "상품 목록 조회",
            notes = "초기 진입 시 기본 상품 리스트 조회 및 다양한 조건으로 필터링할 수 있는 API")
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
        // 카테고리 코드로 카테고리 정보 조회
        Map<String, Object> category = categoryService.getCategoryByCode(categoryCode);

        if (category == null) {
            return ApiResponse.fail(ResponseCode.CATEGORY_NOT_FOUND);
        }

        // 카테고리 ID로 하위 카테고리 목록 조회
        Long categoryId = Long.valueOf(category.get("id").toString());
        List<Map<String, Object>> subcategories = categoryService.getSubcategoriesByCategoryId(categoryId);

        return ApiResponse.success(ResponseCode.SUBCATEGORY_SUCCESS, subcategories);
    }

    @ApiOperation(value = "필터 옵션 조회",
            notes = "카테고리별 사용 가능한 필터 옵션을 제공합니다.")
    @GetMapping("/filter-options")
    public ApiResponse<?> getFilterOptions(
            @RequestParam(required = false, defaultValue = "deposit") String category
    ) {
        // 기본 응답값 (샘플 데이터)
        Map<String, Object> filterOptions = new HashMap<>();

        // 카테고리에 따라 다른 필터 옵션 제공
        switch (category) {
            case "deposit":
            case "saving":
                // 예금/적금 공통 필터 옵션
                filterOptions.put("interestRateTypes", Arrays.asList(
                        new HashMap<String, String>() {{
                            put("code", "S");
                            put("name", "단리");
                        }},
                        new HashMap<String, String>() {{
                            put("code", "M");
                            put("name", "복리");
                        }}
                ));
                filterOptions.put("saveTerms", Arrays.asList(1, 3, 6, 12, 24, 36));
                filterOptions.put("joinMethods", Arrays.asList("전체", "온라인", "오프라인"));
                break;

            case "pension":
                // 연금 전용 필터 옵션
                filterOptions.put("pensionTypes", Arrays.asList(
                        new HashMap<String, String>() {{
                            put("code", "personal");
                            put("name", "개인연금");
                        }},
                        new HashMap<String, String>() {{
                            put("code", "retirement");
                            put("name", "퇴직연금");
                        }}
                ));
                filterOptions.put("guaranteeRates", Arrays.asList(2.0, 2.5, 3.0, 3.5));
                filterOptions.put("saveTerms", Arrays.asList(10, 15, 20, 30));
                break;

            default:
                // 기본 필터 옵션
                filterOptions.put("saveTerms", Arrays.asList(1, 3, 6, 12, 24, 36));
                filterOptions.put("joinMethods", Arrays.asList("전체", "온라인", "오프라인"));
        }
        return ApiResponse.success(ResponseCode.PRODUCT_FILTER_OPTIONS_SUCCESS, filterOptions);
    }

    @ApiOperation(value = "상품 상세 정보 조회",
            notes = "상품 유형과 코드 기반으로 상품 상세 정보를 제공합니다.")
    @GetMapping("/{productType}/{productId}")
    public ApiResponse<?> getProductDetail(
            @PathVariable String productType,
            @PathVariable String productId) {

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
            @RequestParam List<String> productIds) {

        // 기본 응답값 (샘플 데이터)
        List<DepositProductDTO> products = new ArrayList<>();
        for (String productId : productIds) {
            products.add(createSampleDepositProduct(productId));
        }

        Map<String, Object> comparisonData = new HashMap<>();

        // 기본 정보 비교
        Map<String, List<String>> basicInfo = new HashMap<>();
        basicInfo.put("은행명", products.stream().map(DepositProductDTO::getKorCoNm).collect(Collectors.toList()));
        basicInfo.put("상품명", products.stream().map(DepositProductDTO::getFinPrdtNm).collect(Collectors.toList()));
        comparisonData.put("basicInfo", basicInfo);

        // 금리 정보 비교
        Map<String, List<Double>> interestInfo = new HashMap<>();
        interestInfo.put("기본금리", products.stream()
                .map(p -> p.getOptions().get(0).getIntrRate())
                .collect(Collectors.toList()));
        interestInfo.put("최고금리", products.stream()
                .map(p -> p.getOptions().get(0).getIntrRate2())
                .collect(Collectors.toList()));
        comparisonData.put("interestInfo", interestInfo);

        // 가입 조건 비교
        Map<String, List<String>> conditionInfo = new HashMap<>();
        conditionInfo.put("가입방법", products.stream().map(DepositProductDTO::getJoinWay).collect(Collectors.toList()));
        conditionInfo.put("가입대상", products.stream().map(DepositProductDTO::getJoinMember).collect(Collectors.toList()));
        comparisonData.put("conditionInfo", conditionInfo);

        ProductCompareResponse response = ProductCompareResponse.builder()
                .productType("deposit")
                .products(products)
                .comparisonData(comparisonData)
                .build();

        return ApiResponse.success(ResponseCode.PRODUCT_COMPARISON_SUCCESS, response);
    }
}
