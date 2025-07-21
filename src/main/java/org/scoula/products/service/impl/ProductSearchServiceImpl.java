package org.scoula.products.service.impl;

import org.scoula.products.dto.request.ProductSearchRequest;
import org.scoula.products.dto.response.ProductDetailResponse;
import org.scoula.products.dto.response.ProductListResponse;
import org.scoula.products.dto.response.deposit.DepositOptionDTO;
import org.scoula.products.dto.response.deposit.DepositProductDTO;
import org.scoula.products.exception.InvalidProductTypeException;
import org.scoula.products.exception.ProductNotFoundException;
import org.scoula.products.service.ProductSearchService;
import org.scoula.response.ResponseCode;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 금융 상품 검색 서비스 구현체
 */
@Service
public class ProductSearchServiceImpl implements ProductSearchService {

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

        if (filters.containsKey("interestRateType")) {
            request.setIntrRateType(filters.get("interestRateType"));
        }

        if (filters.containsKey("joinMethod")) {
            request.setJoinWay(filters.get("joinMethod"));
        }

        // 기존 메서드 호출하여 검색 수행
        return searchProducts(request);
    }

    @Override
    public ProductListResponse searchProducts(ProductSearchRequest request) {
        // 모의 데이터 생성
        List<ProductListResponse.ProductSummary> mockProducts = createMockProducts(request.getProductType());

        // 키워드 검색 로직 추가
        if (request.getSearchText() != null && !request.getSearchText().isEmpty()) {
            mockProducts = mockProducts.stream()
                    .filter(p -> p.getFinPrdtNm().contains(request.getSearchText()) ||
                            p.getKorCoNm().contains(request.getSearchText()))
                    .collect(Collectors.toList());
        }

        // 필터링 (예시: 최소 금리 필터링)
        if (request.getMinIntrRate() != null) {
            mockProducts = mockProducts.stream()
                    .filter(p -> p.getIntrRate() >= request.getMinIntrRate())
                    .collect(Collectors.toList());
        }

        // 정렬 (예시: 금리 기준 정렬)
        if ("intrRate".equals(request.getSortBy())) {
            if ("desc".equals(request.getSortDirection())) {
                mockProducts.sort((p1, p2) -> Double.compare(p2.getIntrRate(), p1.getIntrRate()));
            } else {
                mockProducts.sort((p1, p2) -> Double.compare(p1.getIntrRate(), p2.getIntrRate()));
            }
        }

        // 페이징 처리
        int totalCount = mockProducts.size();
        int pageSize = request.getPageSize();
        int currentPage = request.getPage();
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalCount);

        List<ProductListResponse.ProductSummary> pagedProducts =
                (startIndex < totalCount) ? mockProducts.subList(startIndex, endIndex) : new ArrayList<>();

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

    @Override
    public ProductDetailResponse getProductDetail(String productType, String productId) {
        // 각 상품 타입별 상세 정보 조회
        switch (productType.toLowerCase()) {
            case "deposit":
                DepositProductDTO deposit = getMockDepositProduct(productId);
                if (deposit == null) {
                    throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
                }
                return new ProductDetailResponse(deposit);
            case "saving":
                // 적금 상품 상세 정보 (임시로 예금과 동일한 로직 사용)
                DepositProductDTO saving = getMockDepositProduct(productId);
                if (saving == null) {
                    throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
                }
                return new ProductDetailResponse(saving);
            case "pension":
                // 연금 상품 상세 정보 (임시로 예금과 동일한 로직 사용)
                DepositProductDTO pension = getMockDepositProduct(productId);
                if (pension == null) {
                    throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
                }
                return new ProductDetailResponse(pension);
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

    /**
     * 모의 상품 목록 생성
     */
    private List<ProductListResponse.ProductSummary> createMockProducts(String productType) {
        List<ProductListResponse.ProductSummary> products = new ArrayList<>();

        switch (productType.toLowerCase()) {
            case "deposit":
                // 예금 상품 모의 데이터
                products.add(createProductSummary("DP0001", "우리은행", "WR뱅크 정기예금", 3.5, 4.0, 12));
                products.add(createProductSummary("DP0002", "국민은행", "KB Star 정기예금", 3.3, 3.8, 24));
                products.add(createProductSummary("DP0003", "신한은행", "신한 플러스 정기예금", 3.4, 3.9, 36));
                products.add(createProductSummary("DP0004", "하나은행", "하나 두배 정기예금", 3.2, 3.7, 12));
                products.add(createProductSummary("DP0005", "농협은행", "NH왕곡 정기예금", 3.1, 3.6, 24));
                break;
            case "saving":
                // 적금 상품 모의 데이터
                products.add(createProductSummary("SV0001", "우리은행", "WR뱅크 자유적금", 3.8, 4.3, 12));
                products.add(createProductSummary("SV0002", "국민은행", "KB Star 자유적금", 3.6, 4.1, 24));
                products.add(createProductSummary("SV0003", "신한은행", "신한 플러스 자유적금", 3.7, 4.2, 36));
                products.add(createProductSummary("SV0004", "하나은행", "하나 두배 자유적금", 3.5, 4.0, 12));
                products.add(createProductSummary("SV0005", "농협은행", "NH왕곡 자유적금", 3.4, 3.9, 24));
                break;
            case "pension":
                // 연금 상품 모의 데이터
                products.add(createProductSummary("PN0001", "우리은행", "WR뱅크 연금저축", 4.0, 4.5, 12));
                products.add(createProductSummary("PN0002", "국민은행", "KB Star 연금저축", 3.8, 4.3, 24));
                products.add(createProductSummary("PN0003", "신한은행", "신한 플러스 연금저축", 3.9, 4.4, 36));
                products.add(createProductSummary("PN0004", "하나은행", "하나 두배 연금저축", 3.7, 4.2, 12));
                products.add(createProductSummary("PN0005", "농협은행", "NH왕곡 연금저축", 3.6, 4.1, 24));
                break;
            default:
                throw new IllegalArgumentException("지원하지 않는 상품 유형입니다: " + productType);
        }

        return products;
    }

    /**
     * 모의 상품 요약 정보 생성
     */
    private ProductListResponse.ProductSummary createProductSummary(
            String productId, String companyName, String productName,
            double interestRate, double specialRate, int saveTerm) {

        return ProductListResponse.ProductSummary.builder()
                .finPrdtCd(productId)
                .korCoNm(companyName)
                .finPrdtNm(productName)
                .intrRate(interestRate)
                .intrRate2(specialRate)
                .saveTrm(saveTerm)
                .joinWay("인터넷뱅킹,스마트폰뱅킹,창구")
                .build();
    }

    /**
     * 모의 예금 상품 상세 정보 조회
     */
    private DepositProductDTO getMockDepositProduct(String productId) {
        // 예금 상품 모의 데이터
        List<DepositProductDTO> products = new ArrayList<>();

        // 상품 1
        DepositProductDTO product1 = new DepositProductDTO();
        product1.setFinPrdtCd("DP0001");
        product1.setFinCoNo("WR001");
        product1.setKorCoNm("우리은행");
        product1.setFinPrdtNm("WR뱅크 정기예금");
        product1.setJoinWay("인터넷뱅킹,스마트폰뱅킹,창구");
        product1.setMtrtInt("만기 후 자동 해지");
        product1.setSpclCnd("우리은행 급여통장 보유 고객 우대금리 0.5% 적용");
        product1.setJoinMember("제한없음");
        product1.setJoinDeny("없음");
        product1.setJoinAmt(10000000L);
        product1.setDclsStrtDay("20230101");

        // 옵션 추가
        List<DepositOptionDTO> options1 = new ArrayList<>();
        DepositOptionDTO option1 = new DepositOptionDTO();
        option1.setFinPrdtCd("DP0001");
        option1.setSaveTrm(12);
        option1.setIntrRateType("S");
        option1.setIntrRateTypeNm("단리");
        option1.setIntrRate(3.5);
        option1.setIntrRate2(4.0);
        options1.add(option1);

        product1.setOptions(options1);
        products.add(product1);

        // 상품 2
        DepositProductDTO product2 = new DepositProductDTO();
        product2.setFinPrdtCd("DP0002");
        product2.setFinCoNo("KB001");
        product2.setKorCoNm("국민은행");
        product2.setFinPrdtNm("KB Star 정기예금");
        product2.setJoinWay("인터넷뱅킹,스마트폰뱅킹,창구");
        product2.setMtrtInt("만기 후 자동 해지");
        product2.setSpclCnd("국민은행 KB Star 멤버십 고객 우대금리 0.5% 적용");
        product2.setJoinMember("제한없음");
        product2.setJoinDeny("없음");
        product2.setJoinAmt(10000000L);
        product2.setDclsStrtDay("20230101");

        // 옵션 추가
        List<DepositOptionDTO> options2 = new ArrayList<>();
        DepositOptionDTO option2 = new DepositOptionDTO();
        option2.setFinPrdtCd("DP0002");
        option2.setSaveTrm(24);
        option2.setIntrRateType("S");
        option2.setIntrRateTypeNm("단리");
        option2.setIntrRate(3.3);
        option2.setIntrRate2(3.8);
        options2.add(option2);

        product2.setOptions(options2);
        products.add(product2);

        // 추가 상품들...

        // 상품 ID로 조회
        return products.stream()
                .filter(p -> p.getFinPrdtCd().equals(productId))
                .findFirst()
                .orElse(null);
    }
}