package org.scoula.products.service.impl;

import org.scoula.products.dto.request.ProductSearchRequest;
import org.scoula.products.dto.response.FilterOptionsResponse;
import org.scoula.products.dto.response.ProductDetailResponse;
import org.scoula.products.dto.response.ProductListResponse;
import org.scoula.products.dto.response.deposit.DepositOptionDTO;
import org.scoula.products.dto.response.deposit.DepositProductDTO;
import org.scoula.products.dto.response.pension.PensionOptionDTO;
import org.scoula.products.dto.response.pension.PensionProductDTO;
import org.scoula.products.exception.InvalidProductTypeException;
import org.scoula.products.exception.ProductNotFoundException;
import org.scoula.products.mapper.DepositProductMapper;
import org.scoula.products.mapper.FinancialProductMapper;
import org.scoula.products.mapper.PensionProductMapper;
import org.scoula.products.service.ProductSearchService;
import org.scoula.response.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 금융 상품 검색 서비스 구현체
 */
@Service
public class ProductSearchServiceImpl implements ProductSearchService {
    // MyBatis Mapper 주입
    private final FinancialProductMapper financialProductMapper;
    private final DepositProductMapper depositProductMapper;
    private final PensionProductMapper pensionProductMapper;

    @Autowired
    public ProductSearchServiceImpl(
            FinancialProductMapper financialProductMapper,
            DepositProductMapper depositProductMapper,
            PensionProductMapper pensionProductMapper) {
        this.financialProductMapper = financialProductMapper;
        this.depositProductMapper = depositProductMapper;
        this.pensionProductMapper = pensionProductMapper;
    }

    @Override
    public ProductListResponse searchProducts(String keyword, Map<String, String> filters, int pageNo) {
        // 인자를 ProductSearchRequest로 변환
        ProductSearchRequest request = new ProductSearchRequest();

        // 카테고리 ID 설정 - 기본값은 예금(1)
        request.setCategoryId(1L);

        // 서브카테고리 설정 - 필터에서 직접 추출
        if (filters.containsKey("subCategory")) {
            try {
                request.setSubCategoryId(Long.parseLong(filters.get("subCategory")));
            } catch (NumberFormatException e) {
                // 서브카테고리 파싱 에러 처리
            }
        } else if (filters.containsKey("category")) {
            // 기존 코드와의 호환성을 위해 "category" 값에 따라 서브카테고리 매핑
            String category = filters.get("category");
            if ("deposit".equals(category)) {
                request.setSubCategoryId(101L);  // 정기예금
            } else if ("pension".equals(category)) {
                request.setCategoryId(5L);  // 연금 카테고리
            }
        }

        // productType 설정 (호환성 유지)
        String productType = filters.getOrDefault("category", "deposit");
        request.setProductType(productType);

        // 검색어 설정
        request.setSearchText(keyword);

        // 정렬 파라미터 처리
        String sort = filters.getOrDefault("sort", "created_at");
        String order = filters.getOrDefault("order", "desc");
        request.setSortBy(sort);
        request.setSortDirection(order);

        // 페이징 설정
        request.setPage(pageNo);
        request.setPageSize(10); // 기본값 설정

        // 금액 필터 설정 - 서브카테고리별 다른 필드 사용
        if (filters.containsKey("amount")) {
            try {
                Long amount = Long.parseLong(filters.get("amount"));

                // 서브카테고리에 따라 다른 필드에 값 설정
                if (request.getSubCategoryId() != null) {
                    if (request.getSubCategoryId() == 101 || request.getSubCategoryId() == 103) {
                        // 정기예금/입출금예금 - 예치 금액
                        request.setDepositAmount(amount);
                    } else if (request.getSubCategoryId() == 102 || request.getSubCategoryId() == 104) {
                        // 자유적금/정기적금 - 월 납입 금액
                        request.setMonthlyPayment(amount);
                    }
                } else {
                    // 서브카테고리 ID가 없을 경우 예치 금액으로 기본 설정
                    request.setDepositAmount(amount);
                }
            } catch (NumberFormatException e) {
                // 금액 파싱 에러 처리
            }
        }

        // 기타 필터 설정
        if (filters.containsKey("saveTerm")) {
            try {
                request.setSaveTrm(Integer.parseInt(filters.get("saveTerm")));
            } catch (NumberFormatException e) {
                // 저축 기간 파싱 에러 처리
            }
        }

        // 최소 금리 필터 설정
        if (filters.containsKey("minIntrRate")) {
            request.setMinIntrRate(Double.parseDouble(filters.get("minIntrRate")));
        }

        if (filters.containsKey("interestRateType")) {
            try {
                request.setIntrRateType(filters.get("interestRateType"));
            } catch (NumberFormatException e) {
                // 금리 유형 파싱 에러 처리
            }
        }

        if (filters.containsKey("joinMethod")) {
            String joinMethod = filters.get("joinMethod");
            if (!"전체".equals(joinMethod)) {
                request.setJoinWay(joinMethod);
            } else {
                request.setJoinWay(null); // "전체"인 경우 null로 설정
            }
        }

        // 은행명 필터 처리 추가
        if (filters.containsKey("banks")) {
            String banksStr = filters.get("banks");
            System.out.println("Banks 파라미터 (원본): " + banksStr);
            if (banksStr != null && !banksStr.equals("전체")) {
                request.setBankStr(banksStr);

                List<String> banks = Arrays.asList(banksStr.split(","));
                System.out.println("Banks 파라미터 (변환 후): " + banks);
                request.setBanks(banks);
            }
        }

        // 기존 메서드 호출하여 검색 수행
        return searchProducts(request);
    }

    @Override
    public ProductListResponse searchProducts(ProductSearchRequest request) {
        // 서브카테고리 ID가 없으면 기본값 설정
        if (request.getSubCategoryId() == null) {
            // productType 기반으로 서브카테고리 설정 (호환성 유지)
            if (request.getProductType() != null) {
                if ("deposit".equals(request.getProductType())) {
                    request.setSubCategoryId(101L);  // 정기예금
                }
            } else {
                request.setSubCategoryId(101L);  // 기본값: 정기예금
            }
        }

        // 카테고리 ID가 없으면 기본값 설정
        if (request.getCategoryId() == null) {
            // 연금 상품은 카테고리 ID 5
            if (request.getProductType() != null && "pension".equals(request.getProductType())) {
                request.setCategoryId(5L);  // 연금 카테고리
            } else {
                request.setCategoryId(1L);  // 예금 카테고리
            }
        }

        // 카테고리 이름 얻기
        String categoryName = mapCategoryIdToName(request.getCategoryId());

        // 페이징 기본값 설정
        Integer pageSize = request.getPageSize() != null ? request.getPageSize() : 10;
        Integer page = request.getPage() != null ? request.getPage() : 1;
        Integer offset = (page - 1) * pageSize;

        // 금액 필터링 - 서브카테고리에 따라 다른 필드 사용
        Integer minAmount = null;
        if (request.getSubCategoryId() != null) {
            if (request.getSubCategoryId() == 101 || request.getSubCategoryId() == 103) {  // 정기예금, 입출금예금
                minAmount = request.getDepositAmount() != null ?
                        request.getDepositAmount().intValue() : null;
            } else if (request.getSubCategoryId() == 102 || request.getSubCategoryId() == 104) {  // 자유적금, 정기적금
                minAmount = request.getMonthlyPayment() != null ?
                        request.getMonthlyPayment().intValue() : null;
            }
        } else {
            // 서브카테고리 ID가 없을 경우 예치 금액 기본 사용
            minAmount = request.getDepositAmount() != null ?
                    request.getDepositAmount().intValue() : null;
        }

        // banks 리스트를 콤마로 구분된 문자열로 변환
        String banksStr = (request.getBanks() != null && !request.getBanks().isEmpty())
                ? String.join(",", request.getBanks())
                : null;

        // 카테고리 ID에 따라 다른 쿼리 사용
        if (request.getCategoryId() == 5) { // 연금 카테고리
            List<PensionProductDTO> pensionProducts = pensionProductMapper.findPensionProducts(
                    request.getSearchText(),
                    request.getJoinWay(),
                    request.getMinIntrRate()
            );

            // 이 부분을 수정: PensionProductSummary로 변환
            List<ProductListResponse.PensionProductSummary> pensionSummaries = pensionProducts.stream()
                    .map(this::convertToPensionProductSummary) // 새로운 변환 메서드 사용
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
        } else {
            // 일반 금융 상품 조회 로직 (예금 카테고리의 모든 서브카테고리)
            List<Map<String, Object>> products = financialProductMapper.findProducts(
                    null,
                    categoryName,
                    request.getCategoryId(),
                    request.getSubCategoryId(),
                    request.getSearchText(),
                    request.getMinIntrRate(),
                    request.getSaveTrm(),
                    request.getIntrRateType(),
                    request.getJoinWay(),
                    minAmount,
                    request.getSortBy(),
                    request.getSortDirection(),
                    pageSize,
                    offset,
                    banksStr
            );

            // 전체 상품 수 조회
            int totalCount = financialProductMapper.countProducts(
                    categoryName,
                    request.getCategoryId(),
                    request.getSubCategoryId(),
                    request.getSearchText(),
                    request.getMinIntrRate(),
                    request.getSaveTrm(),
                    request.getIntrRateType(),
                    request.getJoinWay(),
                    minAmount,
                    banksStr
            );

            List<ProductListResponse.ProductSummary> summaries = products.stream()
                    .map(this::convertToProductSummary)
                    .collect(Collectors.toList());

            // 응답 구성
            return ProductListResponse.builder()
                    .productType(request.getProductType())
                    .categoryId(request.getCategoryId())
                    .subcategoryId(request.getSubCategoryId())
                    .products(summaries)
                    .totalCount(totalCount)
                    .currentPage(page)
                    .pageSize(pageSize)
                    .totalPages((int) Math.ceil((double) totalCount / pageSize))
                    .sortBy(request.getSortBy())
                    .sortDirection(request.getSortDirection())
                    .build();
        }
    }

    // PensionProductDTO를 PensionProductSummary로 변환하는 메서드
    private ProductListResponse.PensionProductSummary convertToPensionProductSummary(PensionProductDTO pension) {
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

    private String mapCategoryName(String categoryName) {
        if ("deposit".equals(categoryName) || "pension".equals(categoryName)) {
            return categoryName.equals("deposit") ? "예금" : "연금";
        }
        return categoryName;
    }

    /**
     * 카테고리 ID를 카테고리 이름으로 변환하는 메서드 (역매핑)
     */
    private String mapCategoryIdToName(Long categoryId) {
        if (categoryId == null) {
            return "예금";
        }

        switch (categoryId.intValue()) {
            case 1:
                return "예금";
            case 2:
                return "대출";
            case 3:
                return "펀드";
            case 4:
                return "보험";
            case 5:
                return "연금";
            case 6:
                return "부동산";
            default:
                return "예금";
        }
    }

    private ProductListResponse.ProductSummary convertPensionDTOToSummary(PensionProductDTO pension) {
        return ProductListResponse.ProductSummary.builder()
                .finPrdtCd(pension.getFinPrdtCd())
                .korCoNm(pension.getKorCoNm())
                .finPrdtNm(pension.getFinPrdtNm())
                // 연금 상품은 다른 방식으로 금리 정보 제공
                .intrRate(pension.getDclsRate() != null ? pension.getDclsRate() : 0.0)
                .intrRate2(pension.getGuarRate() != null ? pension.getGuarRate() : 0.0)
                // 저축 기간은 해당 없을 수 있음
                .saveTrm(null)
                .intrRateType(null)
                .joinWay(pension.getJoinWay())
                .build();
    }

    // Map 객체를 ProductSummary로 변환하는 메서드
    private ProductListResponse.ProductSummary convertToProductSummary(Map<String, Object> product) {
        return ProductListResponse.ProductSummary.builder()
                // null 체크 추가
                .productId(product.get("product_id") != null ?
                                Long.parseLong(product.get("product_id").toString()) : null)
                .finPrdtCd(product.get("fin_prdt_cd").toString())
                .korCoNm(product.get("kor_co_nm").toString())
                .finPrdtNm(product.get("product_name").toString())
                .intrRate(Double.parseDouble(product.get("intr_rate").toString()))
                .intrRate2(product.get("intr_rate2") != null ?
                        Double.parseDouble(product.get("intr_rate2").toString()) : 0.0)
                .saveTrm(Integer.parseInt(product.get("save_trm").toString()))
                .joinWay(product.get("join_way") != null ?
                        product.get("join_way").toString() : "")
                .intrRateType(product.get("intr_rate_type") != null ?
                        product.get("intr_rate_type").toString() : null)
                .build();
    }

    @Override
    public ProductDetailResponse getProductDetail(String productType, Long productId) {
        System.out.println("요청된 상품 유형: " + productType + ", 상품 ID: " + productId);

        // 카테고리 이름 매핑
        String mappedProductType = mapCategoryName(productType);
        System.out.println("매핑된 상품 유형: " + mappedProductType);

        try {
            // 상품 유형에 따른 처리
            if ("deposit".equals(productType)) {
                // 예금 카테고리의 모든 상품 (정기예금, 자유적금 등)
                DepositProductDTO deposit = depositProductMapper.findByProductId(productId);

                if (deposit != null) {
                    System.out.println("조회된 상품 정보: ID=" + deposit.getFinPrdtCd() + ", 은행=" + deposit.getKorCoNm());
                    // 옵션 정보 조회 - product_id로 조회
                    List<DepositOptionDTO> options = depositProductMapper.findOptionsByProductId(productId);
                    deposit.setOptions(options);
                } else {
                    System.out.println("조회된 상품 정보: 없음");
                    throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
                }

                return new ProductDetailResponse(deposit, "deposit");
            } else if ("pension".equals(productType)) {
                // 연금 상품 조회 - product_id로 조회
                PensionProductDTO pension = pensionProductMapper.findByProductId(productId);

                if (pension != null) {
                    // 옵션 정보 조회 - product_id로 조회
                    List<PensionOptionDTO> options = pensionProductMapper.findOptionsByProductId(productId);
                    pension.setOptions(options);
                } else {
                    throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
                }

                return new ProductDetailResponse(pension, "pension");
            } else {
                throw new InvalidProductTypeException(ResponseCode.INVALID_PRODUCT_TYPE_ERROR);
            }
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("상품 조회 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("상품 상세 정보 조회 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public List<String> autocompleteProducts(String keyword) {
        List<String> suggestions = new ArrayList<>();

        // 간단한 모의 자동완성 구현
        if (keyword == null || keyword.isEmpty()) {
            return suggestions;
        }

        // 모의 데이터 - 실제로는 DB나 API에서 조회할 것
        List<String> allProducts = Arrays.asList(
                "정기예금", "자유적금", "주택청약", "연금저축",
                "청년우대적금", "퇴직연금", "월복리정기예금",
                "ISA", "주택담보대출", "신용대출"
        );

        // 키워드로 시작하는 상품명 필터링
        for (String product : allProducts) {
            if (product.toLowerCase().contains(keyword.toLowerCase())) {
                suggestions.add(product);
            }
        }

        return suggestions;
    }

    /**
     * 카테고리명을 카테고리 ID로 변환
     */
    private Long mapCategoryToId(String categoryName) {
        if ("deposit".equals(categoryName)) {
            return 1L;  // 예금 카테고리 ID
        } else if ("pension".equals(categoryName)) {
            return 5L;  // 연금 카테고리 ID
        }
        return 1L;  // 기본값
    }

    @Override
    public FilterOptionsResponse getFilterOptions(String category, Long subCategoryId) {
        Long categoryId;
        // 받은 파라미터가 있으면 그대로, 없으면 디폴트 101L
        Long resolvedSubcategoryId = (subCategoryId != null) ? subCategoryId : 101L;

        if ("deposit".equals(category)) {
            categoryId = 1L;
            // subcategoryId 위에서 이미 처리됨
        } else if ("pension".equals(category)) {
            categoryId = 5L;
            // pension은 subcategoryId = null 허용
        } else {
            categoryId = 1L;
        }

        // 필터 옵션 빌더
        FilterOptionsResponse.FilterOptionsResponseBuilder builder = FilterOptionsResponse.builder()
                .productType(category)  // 호환성을 위해 유지
                .categoryId(categoryId)
                .subcategoryId(resolvedSubcategoryId);

        // 카테고리 및 서브카테고리별 필터 옵션 구성
        if (categoryId == 1) {  // 예금 카테고리
            // 금리 유형 옵션
            List<Map<String, String>> interestRateTypes = new ArrayList<>();
            interestRateTypes.add(createOption("S", "단리"));
            interestRateTypes.add(createOption("M", "복리"));
            builder.interestRateTypes(interestRateTypes);

            // 가입 방법
            builder.joinMethods(Arrays.asList("전체", "온라인", "오프라인"));

            // 은행 목록
            List<String> banks = financialProductMapper.getDistinctBanks();
            if (banks == null || banks.isEmpty()) {
                banks = Arrays.asList(
                        "국민은행", "신한은행", "우리은행", "하나은행", "농협은행",
                        "기업은행", "SC제일은행", "케이뱅크", "카카오뱅크", "토스뱅크"
                );
            }
            builder.banks(banks);

            // 서브카테고리에 따른 추가 옵션
            if (resolvedSubcategoryId == 101 || resolvedSubcategoryId == 103) {  // 정기예금, 입출금예금
                // 저축 기간
                builder.saveTerms(Arrays.asList(1, 3, 6, 12, 24, 36));

                // 예치 금액 옵션
                Map<String, Object> depositAmountOptions = new HashMap<>();
                depositAmountOptions.put("min", 10000);
                depositAmountOptions.put("max", 100000000);
                depositAmountOptions.put("defaultValue", 1000000);
                builder.depositAmountOptions(depositAmountOptions);
            } else if (resolvedSubcategoryId == 102 || resolvedSubcategoryId == 104) {  // 자유적금, 정기적금
                // 저축 기간
                builder.saveTerms(Arrays.asList(6, 12, 24, 36));

                // 월 납입 금액 옵션
                Map<String, Object> monthlyPaymentOptions = new HashMap<>();
                monthlyPaymentOptions.put("min", 10000);
                monthlyPaymentOptions.put("max", 1000000);
                monthlyPaymentOptions.put("defaultValue", 100000);
                builder.monthlyPaymentOptions(monthlyPaymentOptions);
            }
        } else if (categoryId == 5) {  // 연금 카테고리
            // 연금 유형 (DB에서 조회 또는 기본값 사용)
            List<Map<String, String>> pensionTypes = pensionProductMapper.getDistinctPensionTypes(categoryId);
            if (pensionTypes == null || pensionTypes.isEmpty()) {
                pensionTypes = new ArrayList<>();
                pensionTypes.add(createOption("personal", "개인연금"));
                pensionTypes.add(createOption("retirement", "퇴직연금"));
            }
            builder.pensionTypes(pensionTypes);

            // 보장 수익률 (DB에서 조회 또는 기본값 사용)
            List<Double> guaranteeRates = pensionProductMapper.getDistinctGuaranteeRates(categoryId);
            if (guaranteeRates == null || guaranteeRates.isEmpty()) {
                guaranteeRates = Arrays.asList(2.0, 2.5, 3.0, 3.5);
            }
            builder.guaranteeRates(guaranteeRates);

            // 납입 기간 (DB에서 조회 또는 기본값 사용)
            List<Integer> paymentPeriods = pensionProductMapper.getDistinctPaymentPeriods(categoryId);
            if (paymentPeriods == null || paymentPeriods.isEmpty()) {
                paymentPeriods = Arrays.asList(10, 15, 20, 30);
            }
            builder.saveTerms(paymentPeriods);

            // 월 납입금 범위
            Map<String, Object> monthlyPaymentOptions = new HashMap<>();
            Integer minMonthlyPayment = pensionProductMapper.getMinMonthlyPayment(categoryId);
            Integer maxMonthlyPayment = pensionProductMapper.getMaxMonthlyPayment(categoryId);

            monthlyPaymentOptions.put("min", minMonthlyPayment != null ? minMonthlyPayment : 50000);
            monthlyPaymentOptions.put("max", maxMonthlyPayment != null ? maxMonthlyPayment : 1000000);
            monthlyPaymentOptions.put("defaultValue", minMonthlyPayment != null ? minMonthlyPayment : 100000);

            builder.monthlyPaymentOptions(monthlyPaymentOptions);

            // 가입 방식
            builder.joinMethods(Arrays.asList("전체", "온라인", "오프라인"));
        }

        // 서브카테고리 목록 조회
        List<Map<String, Object>> subcategories = financialProductMapper.getSubcategoriesByCategoryId(categoryId);
        if (subcategories == null) {
            subcategories = new ArrayList<>();
        }
        builder.subcategories(subcategories);

        return builder.build();
    }

    // 옵션 생성 도우미 메서드
    private Map<String, String> createOption(String code, String name) {
        Map<String, String> option = new HashMap<>();
        option.put("code", code);
        option.put("name", name);
        return option;
    }
}