package org.scoula.products.service.impl;

import org.scoula.products.dto.request.ProductSearchRequest;
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

        // 상품 유형 설정 (카테고리)
        String productType = filters.getOrDefault("category", "deposit");
        request.setProductType(productType);

        // 검색어 설정
        request.setSearchText(keyword);

        // 페이징 설정
        request.setPage(pageNo);
        request.setPageSize(10); // 기본값 설정

        // 필터 설정
        if (filters.containsKey("saveTerm")) {
            try {
                request.setSaveTrm(Integer.parseInt(filters.get("saveTerm")));
            } catch (NumberFormatException e) {
                // 예외 처리 또는 무시
            }
        }

        // 최소 금리 필터 설정
        if (filters.containsKey("minIntrRate")) {
            try {
                request.setMinIntrRate(Double.parseDouble(filters.get("minIntrRate")));
            } catch (NumberFormatException e) {
                // 예외 처리 또는 무시
            }
        }

        if (filters.containsKey("interestRateType")) {
            request.setIntrRateType(filters.get("interestRateType"));
        }

        if (filters.containsKey("joinMethod")) {
            request.setJoinWay(filters.get("joinMethod"));
        }

        // 기존 메서드 호출하여 검색 수행
        return searchProducts(request);
    }


    private String mapCategoryName(String categoryName) {
        switch (categoryName) {
            case "deposit":
                return "예금";
            case "saving":
                return "적금";
            case "pension":
                return "연금";
            default:
                return categoryName;
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
            .joinWay(pension.getJoinWay())
            .build();
    }

    @Override
    public ProductListResponse searchProducts(ProductSearchRequest request) {
        String mappedProductType = mapCategoryName(request.getProductType());
        List<ProductListResponse.ProductSummary> summaries = new ArrayList<>();

        if("연금저축".equals(mappedProductType)){
            List<PensionProductDTO> pensionProducts = pensionProductMapper.findPensionProducts(
                    request.getSearchText(),
                    request.getJoinWay(),
                    request.getMinIntrRate()
            );

            summaries = pensionProducts.stream()
                    .map(this::convertPensionDTOToSummary)
                    .collect(Collectors.toList());
        }
        else {
                // 예금/적금 상품은 기존 쿼리 사용
                List<Map<String, Object>> products = financialProductMapper.findProducts(
                        mappedProductType,
                        request.getSearchText(),
                        request.getMinIntrRate(),
                        request.getSaveTrm(),
                        request.getIntrRateType(),
                        request.getJoinWay()
                );

                summaries = products.stream()
                        .map(this::convertToProductSummary)
                        .collect(Collectors.toList());
            }

        // 페이징 처리
        int totalCount = summaries.size();
        int pageSize = request.getPageSize();
        int currentPage = request.getPage();
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalCount);

        List<ProductListResponse.ProductSummary> pagedProducts =
                (startIndex < totalCount) ? summaries.subList(startIndex, endIndex) : new ArrayList<>();

        // 응답 구성
        return ProductListResponse.builder()
                .productType(request.getProductType())
                .products(pagedProducts)
                .totalCount(totalCount)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .totalPages((int) Math.ceil((double) totalCount / pageSize))
                .sortBy(request.getSortBy())
                .sortDirection(request.getSortDirection())
                .build();
    }

    // Map 객체를 ProductSummary로 변환하는 메서드
    private ProductListResponse.ProductSummary convertToProductSummary(Map<String, Object> product) {
        return ProductListResponse.ProductSummary.builder()
                .finPrdtCd(product.get("fin_prdt_cd").toString())
                .korCoNm(product.get("kor_co_nm").toString())
                .finPrdtNm(product.get("product_name").toString())
                .intrRate(Double.parseDouble(product.get("intr_rate").toString()))
                .intrRate2(product.get("intr_rate2") != null ?
                        Double.parseDouble(product.get("intr_rate2").toString()) : 0.0)
                .saveTrm(Integer.parseInt(product.get("save_trm").toString()))
                .joinWay(product.get("join_way") != null ?
                        product.get("join_way").toString() : "")
                .build();
    }

    @Override
    public ProductDetailResponse getProductDetail(String productType, String productId) {
        System.out.println("요청된 상품 유형: " + productType + ", 상품 ID: " + productId);

        // 카테고리 이름 매핑
        String mappedProductType = mapCategoryName(productType);

        switch (productType.toLowerCase()) {
            case "deposit":
                // DB에서 예금 상품 조회
                DepositProductDTO deposit = depositProductMapper.findByProductId(productId);

                if (deposit != null) {
                    // 옵션 정보 조회
                    List<DepositOptionDTO> options = depositProductMapper.findOptionsByProductId(productId);
                    deposit.setOptions(options);

                    System.out.println("조회된 상품 정보: ID=" + deposit.getFinPrdtCd() + ", 은행=" + deposit.getKorCoNm());
                } else {
                    System.out.println("조회된 상품 정보: 없음");
                    throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
                }

                return new ProductDetailResponse(deposit, "deposit");

            case "saving":
                // DB에서 적금 상품 조회
                DepositProductDTO saving = depositProductMapper.findByProductId(productId);

                if (saving != null) {
                    // 옵션 정보 조회
                    List<DepositOptionDTO> options = depositProductMapper.findOptionsByProductId(productId);
                    saving.setOptions(options);
                } else {
                    throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
                }

                return new ProductDetailResponse(saving, "saving");

            case "pension":
                // DB에서 연금 상품 조회
                PensionProductDTO pension = pensionProductMapper.findByProductId(productId);

                if (pension != null) {
                    // 옵션 정보 조회
                    List<PensionOptionDTO> options = pensionProductMapper.findOptionsByProductId(productId);
                    pension.setOptions(options);
                } else {
                    throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
                }

                return new ProductDetailResponse(pension, "pension");

            default:
                throw new InvalidProductTypeException(ResponseCode.INVALID_PRODUCT_TYPE_ERROR);
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

//    /**
//     * 모의 상품 목록 생성
//     */
//    private List<ProductListResponse.ProductSummary> createMockProducts(String productType) {
//        List<ProductListResponse.ProductSummary> products = new ArrayList<>();
//
//        switch (productType.toLowerCase()) {
//            case "deposit":
//                // 예금 상품 모의 데이터
//                products.add(createProductSummary("DP0001", "우리은행", "WR뱅크 정기예금", 3.5, 4.0, 12));
//                products.add(createProductSummary("DP0002", "국민은행", "KB Star 정기예금", 3.3, 3.8, 24));
//                products.add(createProductSummary("DP0003", "신한은행", "신한 플러스 정기예금", 3.4, 3.9, 36));
//                products.add(createProductSummary("DP0004", "하나은행", "하나 두배 정기예금", 3.2, 3.7, 12));
//                products.add(createProductSummary("DP0005", "농협은행", "NH왕곡 정기예금", 3.1, 3.6, 24));
//                break;
//            case "saving":
//                // 적금 상품 모의 데이터
//                products.add(createProductSummary("SV0001", "우리은행", "WR뱅크 자유적금", 3.8, 4.3, 12));
//                products.add(createProductSummary("SV0002", "국민은행", "KB Star 자유적금", 3.6, 4.1, 24));
//                products.add(createProductSummary("SV0003", "신한은행", "신한 플러스 자유적금", 3.7, 4.2, 36));
//                products.add(createProductSummary("SV0004", "하나은행", "하나 두배 자유적금", 3.5, 4.0, 12));
//                products.add(createProductSummary("SV0005", "농협은행", "NH왕곡 자유적금", 3.4, 3.9, 24));
//                break;
//            case "pension":
//                // 연금 상품 모의 데이터
//                products.add(createProductSummary("PN0001", "우리은행", "WR뱅크 연금저축", 4.0, 4.5, 12));
//                products.add(createProductSummary("PN0002", "국민은행", "KB Star 연금저축", 3.8, 4.3, 24));
//                products.add(createProductSummary("PN0003", "신한은행", "신한 플러스 연금저축", 3.9, 4.4, 36));
//                products.add(createProductSummary("PN0004", "하나은행", "하나 두배 연금저축", 3.7, 4.2, 12));
//                products.add(createProductSummary("PN0005", "농협은행", "NH왕곡 연금저축", 3.6, 4.1, 24));
//                break;
//            default:
//                throw new IllegalArgumentException("지원하지 않는 상품 유형입니다: " + productType);
//        }
//
//        return products;
//    }
//
//    /**
//     * 모의 상품 요약 정보 생성
//     */
//    private ProductListResponse.ProductSummary createProductSummary(
//            String productId, String companyName, String productName,
//            double interestRate, double specialRate, int saveTerm) {
//
//        return ProductListResponse.ProductSummary.builder()
//                .finPrdtCd(productId)
//                .korCoNm(companyName)
//                .finPrdtNm(productName)
//                .intrRate(interestRate)
//                .intrRate2(specialRate)
//                .saveTrm(saveTerm)
//                .joinWay("인터넷뱅킹,스마트폰뱅킹,창구")
//                .build();
//    }
//
//    /**
//     * 모의 예금 상품 상세 정보 조회
//     */
//    private DepositProductDTO getMockDepositProduct(String productId) {
//        // 예금 상품 데이터 맵 (ID -> 상품 객체)
//        Map<String, DepositProductDTO> depositProducts = new HashMap<>();
//
//        // DP0001 상품 정보
//        DepositProductDTO product1 = new DepositProductDTO();
//        product1.setFinPrdtCd("DP0001");
//        product1.setFinCoNo("WR001");
//        product1.setKorCoNm("우리은행");
//        product1.setFinPrdtNm("WR뱅크 정기예금");
//        product1.setJoinWay("인터넷뱅킹,스마트폰뱅킹,창구");
//        product1.setMtrtInt("만기 후 자동 해지");
//        product1.setSpclCnd("우리은행 급여통장 보유 고객 우대금리 0.5% 적용");
//        product1.setJoinMember("제한없음");
//        product1.setJoinDeny("없음");
//        product1.setJoinAmt(10000000L);
//        product1.setDclsStrtDay("20230101");
//
//        // DP0001 옵션 정보
//        List<DepositOptionDTO> options1 = new ArrayList<>();
//        DepositOptionDTO option1 = new DepositOptionDTO();
//        option1.setFinPrdtCd("DP0001");
//        option1.setSaveTrm(12);
//        option1.setIntrRateType("S");
//        option1.setIntrRateTypeNm("단리");
//        option1.setIntrRate(3.5);
//        option1.setIntrRate2(4.0);
//        options1.add(option1);
//        product1.setOptions(options1);
//
//        depositProducts.put("DP0001", product1);
//
//        // DP0002 상품 정보
//        DepositProductDTO product2 = new DepositProductDTO();
//        product2.setFinPrdtCd("DP0002");
//        product2.setFinCoNo("KB001");
//        product2.setKorCoNm("국민은행");
//        product2.setFinPrdtNm("KB Star 정기예금");
//        product2.setJoinWay("인터넷뱅킹,스마트폰뱅킹,창구");
//        product2.setMtrtInt("만기 후 자동 해지");
//        product2.setSpclCnd("국민은행 KB Star 멤버십 고객 우대금리 0.5% 적용");
//        product2.setJoinMember("제한없음");
//        product2.setJoinDeny("없음");
//        product2.setJoinAmt(10000000L);
//        product2.setDclsStrtDay("20230101");
//
//        // DP0002 옵션 정보
//        List<DepositOptionDTO> options2 = new ArrayList<>();
//        DepositOptionDTO option2 = new DepositOptionDTO();
//        option2.setFinPrdtCd("DP0002");
//        option2.setSaveTrm(24);
//        option2.setIntrRateType("S");
//        option2.setIntrRateTypeNm("단리");
//        option2.setIntrRate(3.3);
//        option2.setIntrRate2(3.8);
//        options2.add(option2);
//        product2.setOptions(options2);
//
//        depositProducts.put("DP0002", product2);
//
//        // DP0003 상품 정보 (신한은행)
//        DepositProductDTO product3 = new DepositProductDTO();
//        product3.setFinPrdtCd("DP0003");
//        product3.setFinCoNo("SH001");
//        product3.setKorCoNm("신한은행");
//        product3.setFinPrdtNm("신한 플러스 정기예금");
//        product3.setJoinWay("인터넷뱅킹,스마트폰뱅킹,창구");
//        product3.setMtrtInt("만기 후 자동 해지");
//        product3.setSpclCnd("신한은행 주거래 고객 우대금리 0.3% 적용");
//        product3.setJoinMember("제한없음");
//        product3.setJoinDeny("없음");
//        product3.setJoinAmt(10000000L);
//        product3.setDclsStrtDay("20230101");
//
//        // DP0003 옵션 정보
//        List<DepositOptionDTO> options3 = new ArrayList<>();
//        DepositOptionDTO option3 = new DepositOptionDTO();
//        option3.setFinPrdtCd("DP0003");
//        option3.setSaveTrm(36);
//        option3.setIntrRateType("S");
//        option3.setIntrRateTypeNm("단리");
//        option3.setIntrRate(3.4);
//        option3.setIntrRate2(3.9);
//        options3.add(option3);
//        product3.setOptions(options3);
//
//        depositProducts.put("DP0003", product3);
//
//        // 필요에 따라 더 많은 상품 추가...
//
//
//        // 상품 ID로 조회
//        return depositProducts.get(productId);
//    }
//
//    /**
//     * 모의 적금 상품 상세 정보 조회
//     */
//    private DepositProductDTO getMockSavingProduct(String productId) {
//        // 적금 상품 데이터 맵 (ID -> 상품 객체)
//        Map<String, DepositProductDTO> savingProducts = new HashMap<>();
//
//        // SV0001 상품 정보
//        DepositProductDTO product1 = new DepositProductDTO();
//        product1.setFinPrdtCd("SV0001");
//        product1.setFinCoNo("WR001");
//        product1.setKorCoNm("우리은행");
//        product1.setFinPrdtNm("WR뱅크 자유적금");
//        product1.setJoinWay("인터넷뱅킹,스마트폰뱅킹,창구");
//        product1.setMtrtInt("만기 후 자동 해지");
//        product1.setSpclCnd("우리은행 급여통장 보유 고객 우대금리 0.5% 적용");
//        product1.setJoinMember("제한없음");
//        product1.setJoinDeny("없음");
//        product1.setJoinAmt(10000000L);
//        product1.setDclsStrtDay("20230101");
//
//        // SV0001 옵션 정보
//        List<DepositOptionDTO> options1 = new ArrayList<>();
//        DepositOptionDTO option1 = new DepositOptionDTO();
//        option1.setFinPrdtCd("SV0001");
//        option1.setSaveTrm(12);
//        option1.setIntrRateType("S");
//        option1.setIntrRateTypeNm("단리");
//        option1.setIntrRate(3.8);
//        option1.setIntrRate2(4.3);
//        options1.add(option1);
//        product1.setOptions(options1);
//
//        savingProducts.put("SV0001", product1);
//
//        // 상품 ID로 조회
//        return savingProducts.get(productId);
//    }
//
//    /**
//     * 모의 연금 상품 상세 정보 조회
//     */
//    private DepositProductDTO getMockPensionProduct(String productId) {
//        // 연금 상품 데이터 맵 (ID -> 상품 객체)
//        Map<String, DepositProductDTO> pensionProducts = new HashMap<>();
//
//        // PN0001 상품 정보
//        DepositProductDTO product1 = new DepositProductDTO();
//        product1.setFinPrdtCd("PN0001");
//        product1.setFinCoNo("WR001");
//        product1.setKorCoNm("우리은행");
//        product1.setFinPrdtNm("WR뱅크 연금저축");
//        product1.setJoinWay("인터넷뱅킹,스마트폰뱅킹,창구");
//        product1.setMtrtInt("만기 후 자동 해지");
//        product1.setSpclCnd("우리은행 급여통장 보유 고객 우대금리 0.5% 적용");
//        product1.setJoinMember("제한없음");
//        product1.setJoinDeny("없음");
//        product1.setJoinAmt(10000000L);
//        product1.setDclsStrtDay("20230101");
//
//        // PN0001 옵션 정보
//        List<DepositOptionDTO> options1 = new ArrayList<>();
//        DepositOptionDTO option1 = new DepositOptionDTO();
//        option1.setFinPrdtCd("PN0001");
//        option1.setSaveTrm(12);
//        option1.setIntrRateType("S");
//        option1.setIntrRateTypeNm("단리");
//        option1.setIntrRate(4.0);
//        option1.setIntrRate2(4.5);
//        options1.add(option1);
//        product1.setOptions(options1);
//
//        pensionProducts.put("PN0001", product1);
//
//        // PN0002 상품 정보
//        DepositProductDTO product2 = new DepositProductDTO();
//        product2.setFinPrdtCd("PN0002");
//        product2.setFinCoNo("KB001");
//        product2.setKorCoNm("국민은행");
//        product2.setFinPrdtNm("KB Star 연금저축");
//        product2.setJoinWay("인터넷뱅킹,스마트폰뱅킹,창구");
//        product2.setMtrtInt("만기 후 자동 해지");
//        product2.setSpclCnd("국민은행 KB Star 멤버십 고객 우대금리 0.5% 적용");
//        product2.setJoinMember("제한없음");
//        product2.setJoinDeny("없음");
//        product2.setJoinAmt(10000000L);
//        product2.setDclsStrtDay("20230101");
//
//        // PN0002 옵션 정보
//        List<DepositOptionDTO> options2 = new ArrayList<>();
//        DepositOptionDTO option2 = new DepositOptionDTO();
//        option2.setFinPrdtCd("PN0002");
//        option2.setSaveTrm(24);
//        option2.setIntrRateType("S");
//        option2.setIntrRateTypeNm("단리");
//        option2.setIntrRate(3.8);
//        option2.setIntrRate2(4.3);
//        options2.add(option2);
//        product2.setOptions(options2);
//
//        pensionProducts.put("PN0002", product2);
//
//        // 상품 ID로 조회
//        return pensionProducts.get(productId);
//    }
}