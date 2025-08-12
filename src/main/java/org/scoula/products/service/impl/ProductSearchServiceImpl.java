package org.scoula.products.service.impl;

import org.scoula.products.dto.response.ProductDTO;
import org.scoula.products.dto.response.deposit.DepositProductDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.scoula.products.dto.request.ProductSearchRequest;
import org.scoula.products.dto.response.FilterOptionsResponse;
import org.scoula.products.dto.response.ProductDetailResponse;
import org.scoula.products.dto.response.ProductListResponse;
import org.scoula.products.dto.response.pension.PensionProductDTO;
import org.scoula.products.exception.ProductNotFoundException;
import org.scoula.products.mapper.DepositProductMapper;
import org.scoula.products.mapper.FinancialProductMapper;
import org.scoula.products.mapper.PensionProductMapper;
import org.scoula.products.service.ProductSearchService;
import org.scoula.products.service.detail.DepositDetailService;
import org.scoula.products.service.detail.PensionDetailService;
import org.scoula.products.service.detail.ProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 금융 상품 검색 서비스 구현체
 */
@Service
public class ProductSearchServiceImpl implements ProductSearchService {
    private static final Logger log = LoggerFactory.getLogger(ProductSearchServiceImpl.class);

    // 상수 정의
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final String DEFAULT_SORT = "created_at";
    private static final String DEFAULT_ORDER = "desc";

    // Mapper 필드
    private final FinancialProductMapper financialProductMapper;
    private final DepositProductMapper depositProductMapper;
    private final PensionProductMapper pensionProductMapper;

    // 상품 상세 서비스
    private final ProductDetailService depositDetailService;
    private final ProductDetailService pensionDetailService;

    @Autowired
    public ProductSearchServiceImpl(
            FinancialProductMapper financialProductMapper,
            DepositProductMapper depositProductMapper,
            PensionProductMapper pensionProductMapper) {
        this.financialProductMapper = financialProductMapper;
        this.depositProductMapper = depositProductMapper;
        this.pensionProductMapper = pensionProductMapper;

        // 상세 서비스 초기화
        this.depositDetailService = new DepositDetailService(depositProductMapper);
        this.pensionDetailService = new PensionDetailService(pensionProductMapper);
    }

    /**
     * 상품 카테고리 Enum (Magic String 제거)
     */
    public enum CategoryType {
        DEPOSIT(1L, "예금"),
        LOAN(2L, "대출"),
        FUND(3L, "펀드"),
        INSURANCE(4L, "보험"),
        PENSION(5L, "연금"),
        REAL_ESTATE(6L, "부동산");

        private final Long id;
        private final String name;

        CategoryType(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        // ID로 카테고리 타입 찾기 (null 안전 처리)
        public static CategoryType fromId(Long id) {
            if (id == null) {
                return DEPOSIT; // null ID는 기본값 반환
            }

            for (CategoryType type : values()) {
                if (id.equals(type.getId())) {
                    return type;
                }
            }
            return DEPOSIT; // 일치하는 ID가 없으면 기본값 반환
        }

        // 문자열로 카테고리 타입 찾기 (null 안전 처리)
        public static CategoryType fromString(String category) {
            if (category == null) {
                return DEPOSIT; // null 카테고리는 기본값 반환
            }

            if ("pension".equalsIgnoreCase(category)) {
                return PENSION;
            } else if ("deposit".equalsIgnoreCase(category)) {
                return DEPOSIT;
            }

            // 기본값: 예금
            return DEPOSIT;
        }
    }

    /**
     * 서브카테고리 Enum (Magic Number 제거)
     */
    public enum SubCategoryType {
        // 예금 서브카테고리
        REGULAR_DEPOSIT(101L, CategoryType.DEPOSIT, "정기예금"),
        FREE_SAVING(102L, CategoryType.DEPOSIT, "자유적금"),
        DEMAND_DEPOSIT(103L, CategoryType.DEPOSIT, "입출금예금"),
        REGULAR_SAVING(104L, CategoryType.DEPOSIT, "정기적금"),

        // 연금 서브카테고리
        PENSION_SAVING(501L, CategoryType.PENSION, "연금저축"),
        RETIREMENT_PENSION(502L, CategoryType.PENSION, "퇴직연금"),
        PERSONAL_PENSION(503L, CategoryType.PENSION, "개인연금");

        private final Long id;
        private final CategoryType categoryType;
        private final String name;

        SubCategoryType(Long id, CategoryType categoryType, String name) {
            this.id = id;
            this.categoryType = categoryType;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public CategoryType getCategoryType() {
            return categoryType;
        }

        public String getName() {
            return name;
        }

        // ID로 서브카테고리 찾기
        public static SubCategoryType fromId(Long id) {
            if (id == null) return REGULAR_DEPOSIT; // 기본값

            return Arrays.stream(values())
                    .filter(type -> type.getId().equals(id))
                    .findFirst()
                    .orElse(REGULAR_DEPOSIT); // 기본값: 정기예금
        }

        // 카테고리 타입에 맞는 기본 서브카테고리 반환
        public static SubCategoryType getDefaultForCategory(CategoryType categoryType) {
            if (categoryType == CategoryType.PENSION) {
                return PENSION_SAVING;
            }
            return REGULAR_DEPOSIT;
        }

        // 서브카테고리가 예금 유형인지 확인
        public static boolean isDepositType(Long subcategoryId) {
            return REGULAR_DEPOSIT.getId().equals(subcategoryId) ||
                    DEMAND_DEPOSIT.getId().equals(subcategoryId);
        }

        // 서브카테고리가 적금 유형인지 확인
        public static boolean isSavingType(Long subcategoryId) {
            return FREE_SAVING.getId().equals(subcategoryId) ||
                    REGULAR_SAVING.getId().equals(subcategoryId);
        }
    }

    @Override
    public ProductListResponse searchProducts(String keyword, Map<String, String> filters, int pageNo) {
        ProductSearchRequest request = buildSearchRequest(keyword, filters, pageNo);
        return searchProducts(request);
    }

    /**
     * 검색 요청 객체 생성 (리팩토링: 빌더 패턴 적용)
     */
    private ProductSearchRequest buildSearchRequest(String keyword, Map<String, String> filters, int pageNo) {
        ProductSearchRequest request = new ProductSearchRequest();

        // 카테고리 설정
        CategoryType categoryType = parseCategoryType(filters);
        request.setCategoryId(categoryType.getId());
        request.setProductType(categoryType == CategoryType.PENSION ? "pension" : "deposit");

        // 서브카테고리 설정
        Long subCategoryId = parseSubCategoryId(filters, categoryType);
        request.setSubCategoryId(subCategoryId);

        // 검색어 설정
        request.setSearchText(keyword);

        // 정렬 파라미터 처리
        request.setSortBy(filters.getOrDefault("sort", DEFAULT_SORT));
        request.setSortDirection(filters.getOrDefault("order", DEFAULT_ORDER));

        // 페이징 설정
        request.setPage(pageNo);
        request.setPageSize(DEFAULT_PAGE_SIZE);

        // 예치 금액 필터 설정
        setAmountFilter(request, filters, subCategoryId);

        // 저축 기간 필터 설정
        if (filters.containsKey("saveTerm")) {
            try {
                request.setSaveTrm(Integer.parseInt(filters.get("saveTerm")));
            } catch (NumberFormatException e) {
                log.warn("저축 기간 파싱 오류: {}", filters.get("saveTerm"));
            }
        }

        // 최소 금리 필터 설정
        if (filters.containsKey("minIntrRate")) {
            try {
                request.setMinIntrRate(Double.parseDouble(filters.get("minIntrRate")));
            } catch (NumberFormatException e) {
                log.warn("최소 금리 파싱 오류: {}", filters.get("minIntrRate"));
            }
        }

        // 금리 유형 필터 설정
        if (filters.containsKey("interestRateType")) {
            request.setIntrRateType(filters.get("interestRateType"));
        }

        // 가입 방법 다중 선택 처리
        if (filters.containsKey("joinWays")) {
            String joinWaysStr = filters.get("joinWays");
            if (joinWaysStr != null && !joinWaysStr.isEmpty()) {
                List<String> joinWays = Arrays.asList(joinWaysStr.split(","));
                request.setJoinWays(joinWays);
            }
        }
        // 이전 버전 호환성을 위한 단일 선택 처리
        else if (filters.containsKey("joinWay")) {
            String joinWay = filters.get("joinWay");
            if (joinWay != null && !joinWay.isEmpty() && !joinWay.equals("전체")) {
                request.setJoinWay(joinWay);
            }
        }

        // 은행 필터 설정
        if (filters.containsKey("banks")) {
            String banksStr = filters.get("banks");
            if (banksStr != null && !banksStr.equals("전체")) {
                request.setBankStr(banksStr);
                request.setBanks(Arrays.asList(banksStr.split(",")));
            }
        }

        // rsrvType 필터 설정 추가
        if (filters.containsKey("rsrvType")) {
            request.setRsrvType(filters.get("rsrvType"));
        }

        return request;
    }

    /**
     * 카테고리 타입 파싱
     */
    private CategoryType parseCategoryType(Map<String, String> filters) {
        // categoryId 파라미터가 있는 경우
        if (filters.containsKey("categoryId")) {
            try {
                Long categoryId = Long.parseLong(filters.get("categoryId"));
                return CategoryType.fromId(categoryId);
            } catch (NumberFormatException e) {
                log.warn("카테고리 ID 파싱 오류: {}", filters.get("categoryId"));
            }
        }

        // category 파라미터가 있는 경우
        if (filters.containsKey("category")) {
            return CategoryType.fromString(filters.get("category"));
        }

        // 기본값: 예금
        return CategoryType.DEPOSIT;
    }

    /**
     * 서브카테고리 ID 파싱
     */
    private Long parseSubCategoryId(Map<String, String> filters, CategoryType categoryType) {
        // subCategory 파라미터가 있는 경우
        if (filters.containsKey("subCategory")) {
            try {
                return Long.parseLong(filters.get("subCategory"));
            } catch (NumberFormatException e) {
                log.warn("서브카테고리 ID 파싱 오류: {}", filters.get("subCategory"));
            }
        }

        // 연금 카테고리인 경우 null 반환 (서브카테고리 없음)
        if (categoryType == CategoryType.PENSION) {
            return null;
        }

        // 기본값: 정기예금
        return SubCategoryType.REGULAR_DEPOSIT.getId();
    }

    /**
     * 금액 필터 설정
     */
    /**
     * 금액 필터 설정
     *
     * @param request       ProductSearchRequest 객체
     * @param filters       필터 맵
     * @param subCategoryId 서브카테고리 ID
     */
    private void setAmountFilter(ProductSearchRequest request, Map<String, String> filters, Long subCategoryId) {
        // "amount" 필터가 있는 경우 처리
        if (filters.containsKey("depositAmount")) {
            try {
                Long depositAmount = Long.parseLong(filters.get("depositAmount").replaceAll("[^0-9]", ""));

                // 서브카테고리에 따라 다른 필드에 설정
                if (subCategoryId != null) {
                    if (subCategoryId == 101L) { // 정기예금
                        request.setDepositAmount(depositAmount);
                    } else if (subCategoryId == 102L || subCategoryId == 104L) { // 자유적금, 정기적금
                        request.setDepositAmount(depositAmount);
                    } else {
                        // 기본적으로 depositAmount에 설정
                        request.setDepositAmount(depositAmount);
                    }
                } else {
                    // 서브카테고리가 없으면 depositAmount에 설정
                    request.setDepositAmount(depositAmount);
                }

                log.debug("금액 필터 설정: {}", depositAmount);
            } catch (NumberFormatException e) {
                log.warn("금액 파싱 오류: {}", filters.get("depositAmount"));
            }
        }
    }

    @Override
    public ProductListResponse searchProducts(ProductSearchRequest request) {
        // 기본값 설정
        setDefaultValues(request);

        // 카테고리 타입에 따라 다른 처리
        CategoryType categoryType = CategoryType.fromId(request.getCategoryId());

        if (categoryType == CategoryType.PENSION) {
            return searchPensionProducts(request);
        } else {
            return searchDepositProducts(request);
        }
    }

    /**
     * 검색 요청 기본값 설정
     */
    private void setDefaultValues(ProductSearchRequest request) {
        // 서브카테고리 ID가 없으면 기본값 설정
        if (request.getSubCategoryId() == null) {
            if ("pension".equals(request.getProductType())) {
                // 연금은 서브카테고리 null 허용
            } else {
                request.setSubCategoryId(SubCategoryType.REGULAR_DEPOSIT.getId());
            }
        }

        // 카테고리 ID가 없으면 기본값 설정
        if (request.getCategoryId() == null) {
            if ("pension".equals(request.getProductType())) {
                request.setCategoryId(CategoryType.PENSION.getId());
            } else {
                request.setCategoryId(CategoryType.DEPOSIT.getId());
            }
        }
    }

    /**
     * 예금 상품 검색
     */
    private ProductListResponse searchDepositProducts(ProductSearchRequest request) {
        // 페이징 처리
        Integer pageSize = request.getPageSize() != null ? request.getPageSize() : DEFAULT_PAGE_SIZE;
        Integer page = request.getPage() != null ? request.getPage() : 1;
        Integer offset = (page - 1) * pageSize;

        // 금액 필터링
        //Integer minAmount = getMinAmountBySubcategory(request);
        Long depositAmount = request.getDepositAmount();

        // banks 리스트를 콤마로 구분된 문자열로 변환
        String banksStr = (request.getBanks() != null && !request.getBanks().isEmpty())
                ? String.join(",", request.getBanks())
                : null;

        // 카테고리 이름 얻기
        CategoryType categoryType = CategoryType.fromId(request.getCategoryId());
        String categoryName = categoryType.getName();

        // 가입 방법을 콤마로 구분된 문자열로 변환
        String joinWaysStr = null;
        if (request.getJoinWays() != null && !request.getJoinWays().isEmpty()) {
            joinWaysStr = String.join(",", request.getJoinWays());
        }

        // 1. 먼저 필터링된 상품의 총 개수를 확인하기 위해 모든 상품 조회 (페이지네이션 없이)
        // 여기서 minAmount가 이미 금액 필터링의 일부를 처리
        List<ProductDTO> allProducts = financialProductMapper.findProducts(
                null,
                categoryName,
                request.getCategoryId(),
                request.getSubCategoryId(),
                request.getSearchText(),
                request.getMinIntrRate(),
                request.getSaveTrm(),
                request.getIntrRateType(),
                joinWaysStr,
                request.getDepositAmount(),
                request.getSortBy(),
                request.getSortDirection(),
                null, // 페이지 크기를 null로 설정하여 모든 결과 가져오기
                null, // 오프셋도 null로 설정
                banksStr,
                request.getRsrvType()
        );

        // 2. 금액 최대값 필터링 적용 (최소값은 이미 DB에서 필터링됨)
        // 메모리에서 가입 방법 필터링 처리
        if (request.getJoinWays() != null && !request.getJoinWays().isEmpty()) {
            List<String> selectedJoinWays = request.getJoinWays();

            allProducts = allProducts.stream()
                    .filter(product -> {
                        // 상품에 가입 방법 정보가 없으면 포함 안함
                        if (product.getJoinWay() == null || product.getJoinWay().isEmpty()) {
                            return false;
                        }

                        // 상품의 가입 방법을 배열로 분리
                        List<String> productJoinWays = Arrays.asList(product.getJoinWay().split(","));

                        // 선택된 가입 방법 중 하나라도 상품의 가입 방법에 포함되면 결과에 포함
                        return selectedJoinWays.stream()
                                .anyMatch(selectedWay ->
                                        productJoinWays.stream()
                                                .anyMatch(productWay ->
                                                        productWay.trim().equalsIgnoreCase(selectedWay.trim())
                                                )
                                );
                    })
                    .collect(Collectors.toList());
        }
        // 단일 선택 가입 방법 처리 (기존 코드 호환성)
        else if (request.getJoinWay() != null && !request.getJoinWay().isEmpty()) {
            String joinWay = request.getJoinWay();

            allProducts = allProducts.stream()
                    .filter(product -> {
                        if (product.getJoinWay() == null || product.getJoinWay().isEmpty()) {
                            return false;
                        }

                        List<String> productJoinWays = Arrays.asList(product.getJoinWay().split(","));
                        return productJoinWays.stream()
                                .anyMatch(way -> way.trim().equalsIgnoreCase(joinWay.trim()));
                    })
                    .collect(Collectors.toList());
        }

        // 3. 필터링된 전체 상품 수 계산
        int totalCount = allProducts.size();

        // 4. 필터링된 전체 상품에서 해당 페이지의 상품만 추출
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalCount);

        List<ProductDTO> pagedProducts;
        if (startIndex < totalCount) {
            pagedProducts = allProducts.subList(startIndex, endIndex);
        } else {
            pagedProducts = new ArrayList<>();
        }

        // 결과 변환
        List<ProductListResponse.ProductSummary> summaries = pagedProducts.stream()
                .map(this::convertToProductSummary)
                .collect(Collectors.toList());

        // 응답 구성
        return ProductListResponse.builder()
                .productType(request.getProductType())
                .categoryId(request.getCategoryId())
                .subcategoryId(request.getSubCategoryId())
                .products(summaries)
                .totalCount(totalCount) // 필터링 후 전체 상품 수
                .currentPage(page)
                .pageSize(pageSize)
                .totalPages((int) Math.ceil((double) totalCount / pageSize))
                .sortBy(request.getSortBy())
                .sortDirection(request.getSortDirection())
                .build();
    }

    /**
     * 연금 상품 검색
     */
    private ProductListResponse searchPensionProducts(ProductSearchRequest request) {
        // 연금 상품 조회
        List<PensionProductDTO> pensionProducts = pensionProductMapper.findPensionProducts(
                request.getSearchText(),
                request.getJoinWay(),
                request.getMinIntrRate()
        );

        // 페이징 설정
        Integer pageSize = request.getPageSize() != null ? request.getPageSize() : DEFAULT_PAGE_SIZE;
        Integer page = request.getPage() != null ? request.getPage() : 1;

        // 결과 변환
        List<ProductListResponse.PensionProductSummary> pensionSummaries = pensionProducts.stream()
                .map(this::convertToPensionProductSummary)
                .collect(Collectors.toList());

        // 연금 상품은 메모리에서 페이징 처리
        int totalCount = pensionSummaries.size();
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalCount);

        List<ProductListResponse.PensionProductSummary> pagedPensionProducts =
                (startIndex < totalCount) ? pensionSummaries.subList(startIndex, endIndex) : new ArrayList<>();

        return ProductListResponse.builder()
                .productType(request.getProductType())
                .categoryId(request.getCategoryId())
                .subcategoryId(request.getSubCategoryId())
                .products(null) // 일반 products는 null로 설정
                .pensionProducts(pagedPensionProducts) // 여기에 연금 상품 목록 설정
                .totalCount(totalCount)
                .currentPage(page)
                .pageSize(pageSize)
                .totalPages((int) Math.ceil((double) totalCount / pageSize))
                .sortBy(request.getSortBy())
                .sortDirection(request.getSortDirection())
                .build();
    }

    /**
     * 서브카테고리에 따른 최소 금액 필터 설정
     */
    private Integer getMinAmountBySubcategory(ProductSearchRequest request) {
        if (request.getSubCategoryId() != null) {
            if (SubCategoryType.isDepositType(request.getSubCategoryId())) {
                return request.getDepositAmount() != null ?
                        request.getDepositAmount().intValue() : null;
            } else if (SubCategoryType.isSavingType(request.getSubCategoryId())) {
                return request.getMonthlyPayment() != null ?
                        request.getMonthlyPayment().intValue() : null;
            }
        }

        // 서브카테고리 ID가 없을 경우 예치 금액 기본 사용
        return request.getDepositAmount() != null ?
                request.getDepositAmount().intValue() : null;
    }

    /**
     * ProductDTO를 ProductSummary로 변환하는 메서드
     */
    private ProductListResponse.ProductSummary convertToProductSummary(ProductDTO product) {
        if (product == null) {
            throw new IllegalArgumentException("상품 정보가 없습니다.");
        }

        return ProductListResponse.ProductSummary.builder()
                .productId(product.getProductId())
                .finCoNo(product.getFinCoNo())
                .finPrdtCd(product.getFinPrdtCd())
                .korCoNm(product.getKorCoNm())
                .finPrdtNm(product.getProductName()) // product_name -> productName으로 필드명 주의
                .intrRate(product.getIntrRate() != null ? product.getIntrRate() : 0.0)
                .intrRate2(product.getIntrRate2() != null ? product.getIntrRate2() : 0.0)
                .saveTrm(product.getSaveTrm())
                .joinWay(product.getJoinWay() != null ? product.getJoinWay() : "")
                .intrRateType(product.getIntrRateType())
                .minDepositAmount(product.getMinDeposit()) // 최소 예치 금액 추가
                .maxDepositAmount(product.getMaxLimit())  // 최대 예치 금액 추가
                .rsrvType(product.getRsrvType())  // 적립식 유형 추가
                .build();
    }

    /**
     * PensionProductDTO를 PensionProductSummary로 변환하는 메서드
     */
    private ProductListResponse.PensionProductSummary convertToPensionProductSummary(PensionProductDTO pension) {
        if (pension == null) {
            throw new IllegalArgumentException("연금 상품 정보가 없습니다.");
        }

        return ProductListResponse.PensionProductSummary.builder()
                .finPrdtCd(pension.getFinPrdtCd())
                .korCoNm(pension.getKorCoNm())
                .finPrdtNm(pension.getFinPrdtNm())
                .dclsRate(pension.getDclsRate())
                .guarRate(pension.getGuarRate())
                .pnsnKind(pension.getPnsnKind())
                .pnsnKindNm(pension.getPnsnKindNm())
                .prdtType(pension.getPrdtType())
                .prdtTypeNm(pension.getPrdtTypeNm())
                .build();
    }

    /**
     * Map에서 안전하게 문자열 값을 가져오는 메서드
     */
    private String getStringValue(Map<String, Object> map, String key) {
        return getStringValue(map, key, "");
    }

    /**
     * Map에서 안전하게 문자열 값을 가져오는 메서드 (기본값 지정)
     */
    private String getStringValue(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    /**
     * Map에서 안전하게 숫자 값을 가져오는 메서드
     */
    private <T extends Number> T getNumberValue(Map<String, Object> map, String key, Class<T> type) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }

        try {
            if (type == Integer.class) {
                return type.cast(Integer.parseInt(value.toString()));
            } else if (type == Long.class) {
                return type.cast(Long.parseLong(value.toString()));
            } else if (type == Double.class) {
                return type.cast(Double.parseDouble(value.toString()));
            }
        } catch (NumberFormatException e) {
            log.warn("숫자 변환 오류: {} ({})", key, value);
        }

        return null;
    }

    /**
     * Map에서 안전하게 실수 값을 가져오는 메서드 (기본값 지정)
     */
    private Double getDoubleValue(Map<String, Object> map, String key) {
        return getDoubleValue(map, key, 0.0);
    }

    /**
     * Map에서 안전하게 실수 값을 가져오는 메서드 (기본값 지정)
     */
    private Double getDoubleValue(Map<String, Object> map, String key, Double defaultValue) {
        Object value = map.get(key);
        if (value == null) {
            return defaultValue;
        }

        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            log.warn("실수 변환 오류: {} ({})", key, value);
            return defaultValue;
        }
    }

    @Override
    public ProductDetailResponse getProductDetail(String productType, Long productId) {
        log.info("상품 상세 조회 요청 - 유형: {}, ID: {}", productType, productId);

        try {
            // ProductType enum 사용 (Magic String 제거)
            CategoryType type = CategoryType.fromString(productType);
            ProductDetailService detailService = getProductDetailService(type);

            return detailService.getDetail(productId);
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("상품 조회 중 예외 발생", e);
            throw new RuntimeException("상품 상세 정보 조회 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 카테고리 타입에 따른 상세 서비스 반환
     */
    private ProductDetailService getProductDetailService(CategoryType type) {
        switch (type) {
            case PENSION:
                return pensionDetailService;
            case DEPOSIT:
            default:
                return depositDetailService;
        }
    }

    @Override
    public List<String> autocompleteProducts(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return Collections.emptyList();
        }

        // TODO: DB에서 자동완성 키워드 조회 구현
        List<String> allProducts = Arrays.asList(
                "정기예금", "자유적금", "주택청약", "연금저축",
                "청년우대적금", "퇴직연금", "월복리정기예금",
                "ISA", "주택담보대출", "신용대출"
        );

        // 키워드로 시작하는 상품명 필터링
        return allProducts.stream()
                .filter(product -> product.toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public FilterOptionsResponse getFilterOptions(String category, Long subCategoryId) {
        // CategoryType 사용 (Magic String 제거)
        CategoryType categoryType = CategoryType.fromString(category);

        // 서브카테고리 ID (없으면 기본값)
        Long resolvedSubcategoryId = (subCategoryId != null) ?
                subCategoryId :
                SubCategoryType.getDefaultForCategory(categoryType).getId();

        // 빌더 패턴 적용
        FilterOptionsBuilder builder = new FilterOptionsBuilder(categoryType, resolvedSubcategoryId);

        return builder
                .addBasicOptions()
                .addCategorySpecificOptions()
                .addSubcategoryOptions()
                .build();
    }

    /**
     * 필터 옵션 빌더 클래스
     */
    public class FilterOptionsBuilder {
        private final CategoryType categoryType;
        private final Long subcategoryId;
        private final FilterOptionsResponse.FilterOptionsResponseBuilder responseBuilder;

        public FilterOptionsBuilder(CategoryType categoryType, Long subcategoryId) {
            this.categoryType = categoryType;
            this.subcategoryId = subcategoryId;

            this.responseBuilder = FilterOptionsResponse.builder()
                    .productType(categoryType == CategoryType.PENSION ? "pension" : "deposit")
                    .categoryId(categoryType.getId())
                    .subcategoryId(subcategoryId);
        }

        // ProductSearchServiceImpl.java 또는 관련 서비스 클래스에 추가
        private List<DepositProductDTO> filterByAmount(List<DepositProductDTO> products, Map<String, String> filters) {
            if (!filters.containsKey("depositAmount")) {
                return products; // 금액 필터가 없으면 그대로 반환
            }

            try {
                Long amount = Long.parseLong(filters.get("depositAmount"));

                return products.stream()
                        .filter(product -> {
                            // 최소 가입 금액 (null이면 0으로 처리)
                            Long minAmount = product.getJoinAmt() != null ? product.getJoinAmt() : 0L;

                            // 최대 가입 금액 (null이면 제한 없음으로 처리)
                            Long maxAmount = product.getMaxLimit() != null && product.getMaxLimit() > 0 ?
                                    product.getMaxLimit() : Long.MAX_VALUE;

                            // 입력 금액이 최소 이상, 최대 이하인 경우만 포함
                            return minAmount <= amount && amount <= maxAmount;
                        })
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                // 금액 변환 실패 시 로그 출력 후 원본 리스트 반환
                log.warn("Invalid amount format: {}", filters.get("amount"), e);
                return products;
            }
        }

        /**
         * 기본 옵션 추가 (가입 방법 등)
         */
        public FilterOptionsBuilder addBasicOptions() {
            // 가입 방법
            responseBuilder.joinMethods(getJoinMethods());
            // 서브카테고리 목록 조회
            List<Map<String, Object>> subcategories = financialProductMapper.getSubcategoriesByCategoryId(categoryType.getId());
            if (subcategories == null) {
                subcategories = new ArrayList<>();
            }
            responseBuilder.subcategories(subcategories);

            return this;
        }

        /**
         * 카테고리별 특화 옵션 추가
         */
        public FilterOptionsBuilder addCategorySpecificOptions() {
            switch (categoryType) {
                case DEPOSIT:
                    addDepositCategoryOptions();
                    break;
                case PENSION:
                    addPensionCategoryOptions();
                    break;
                default:
                    // 기본 카테고리는 추가 옵션 없음
                    break;
            }
            return this;
        }

        /**
         * 서브카테고리별 특화 옵션 추가
         */
        public FilterOptionsBuilder addSubcategoryOptions() {
            if (categoryType == CategoryType.DEPOSIT && subcategoryId != null) {
                if (SubCategoryType.isDepositType(subcategoryId)) {
                    // 정기예금, 입출금예금 옵션
                    addDepositSubcategoryOptions();
                } else if (SubCategoryType.isSavingType(subcategoryId)) {
                    // 자유적금, 정기적금 옵션
                    addSavingSubcategoryOptions();
                }
            }
            return this;
        }

        /**
         * 최종 필터 옵션 응답 생성
         */
        public FilterOptionsResponse build() {
            return responseBuilder.build();
        }

        /**
         * 가입 방법 목록 반환
         */
        private List<String> getJoinMethods() {
            return Arrays.asList("전화", "영업점", "인터넷", "스마트폰");
        }

        /**
         * 예금 카테고리 특화 옵션 추가
         */
        private void addDepositCategoryOptions() {
            // 금리 유형 옵션
            List<Map<String, String>> interestRateTypes = new ArrayList<>();
            interestRateTypes.add(createOption("S", "단리"));
            interestRateTypes.add(createOption("M", "복리"));
            responseBuilder.interestRateTypes(interestRateTypes);

            // 은행 목록
            List<String> banks = financialProductMapper.getDistinctBanks(categoryType.getId());
            if (banks == null || banks.isEmpty()) {
                banks = Arrays.asList(
                        "국민은행", "신한은행", "우리은행", "하나은행", "농협은행",
                        "기업은행", "SC제일은행", "케이뱅크", "카카오뱅크", "토스뱅크"
                );
            }
            responseBuilder.banks(banks);
        }

        /**
         * 연금 카테고리 특화 옵션 추가
         */
        private void addPensionCategoryOptions() {
            // 연금 유형 (DB에서 조회 또는 기본값 사용)
            List<Map<String, String>> pensionTypes = pensionProductMapper.getDistinctPensionTypes(categoryType.getId());
            if (pensionTypes == null || pensionTypes.isEmpty()) {
                pensionTypes = new ArrayList<>();
                pensionTypes.add(createOption("personal", "개인연금"));
                pensionTypes.add(createOption("retirement", "퇴직연금"));
            }
            responseBuilder.pensionTypes(pensionTypes);

            // 금융사 목록 (연금 카테고리에 맞는 금융사) - 여기를 추가!
            List<String> pensionCompanies = financialProductMapper.getDistinctBanks(categoryType.getId());
            if (pensionCompanies == null || pensionCompanies.isEmpty()) {
                pensionCompanies = Arrays.asList(
                        "미래에셋생명", "삼성생명", "한화생명", "교보생명", "KB생명",
                        "신한라이프", "농협생명", "푸르덴셜생명"
                );
            }
            responseBuilder.banks(pensionCompanies);

            // 보장 수익률 (DB에서 조회 또는 기본값 사용)
            List<Double> guaranteeRates = pensionProductMapper.getDistinctGuaranteeRates(categoryType.getId());
            if (guaranteeRates == null || guaranteeRates.isEmpty()) {
                guaranteeRates = Arrays.asList(2.0, 2.5, 3.0, 3.5);
            }
            responseBuilder.guaranteeRates(guaranteeRates);

            // 납입 기간 (DB에서 조회 또는 기본값 사용)
            List<Integer> paymentPeriods = pensionProductMapper.getDistinctPaymentPeriods(categoryType.getId());
            if (paymentPeriods == null || paymentPeriods.isEmpty()) {
                paymentPeriods = Arrays.asList(10, 15, 20, 30);
            }
            responseBuilder.saveTerms(paymentPeriods);

            // 월 납입금 범위
            Map<String, Object> monthlyPaymentOptions = new HashMap<>();
            Integer minMonthlyPayment = pensionProductMapper.getMinMonthlyPayment(categoryType.getId());
            Integer maxMonthlyPayment = pensionProductMapper.getMaxMonthlyPayment(categoryType.getId());

            monthlyPaymentOptions.put("min", minMonthlyPayment != null ? minMonthlyPayment : 50000);
            monthlyPaymentOptions.put("max", maxMonthlyPayment != null ? maxMonthlyPayment : 1000000);
            monthlyPaymentOptions.put("defaultValue", minMonthlyPayment != null ? minMonthlyPayment : 100000);

            responseBuilder.monthlyPaymentOptions(monthlyPaymentOptions);
        }

        /**
         * 정기예금/입출금예금 서브카테고리 특화 옵션 추가
         */
        private void addDepositSubcategoryOptions() {
            // 저축 기간
            responseBuilder.saveTerms(Arrays.asList(1, 3, 6, 12, 24, 36));

            // 예치 금액 옵션
            Map<String, Object> depositAmountOptions = new HashMap<>();
            depositAmountOptions.put("min", 10000);
            depositAmountOptions.put("max", 100000000);
            depositAmountOptions.put("defaultValue", 1000000);
            responseBuilder.depositAmountOptions(depositAmountOptions);
        }

        /**
         * 자유적금/정기적금 서브카테고리 특화 옵션 추가
         */
        private void addSavingSubcategoryOptions() {
            // 저축 기간
            responseBuilder.saveTerms(Arrays.asList(6, 12, 24, 36));

            // 월 납입 금액 옵션
            Map<String, Object> monthlyPaymentOptions = new HashMap<>();
            monthlyPaymentOptions.put("min", 10000);
            monthlyPaymentOptions.put("max", 1000000);
            monthlyPaymentOptions.put("defaultValue", 100000);
            responseBuilder.monthlyPaymentOptions(monthlyPaymentOptions);
        }
    }

    /**
     * 옵션 생성 도우미 메서드
     */
    private Map<String, String> createOption(String code, String name) {
        Map<String, String> option = new HashMap<>();
        option.put("code", code);
        option.put("name", name);
        return option;
    }
}

