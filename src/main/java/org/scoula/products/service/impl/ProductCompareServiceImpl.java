package org.scoula.products.service.impl;

import org.scoula.products.dto.request.ProductCompareRequest;
import org.scoula.products.dto.response.ProductCompareResponse;
import org.scoula.products.dto.response.deposit.DepositOptionDTO;
import org.scoula.products.dto.response.deposit.DepositProductDTO;
import org.scoula.products.exception.InvalidProductTypeException;
import org.scoula.products.exception.ProductNotFoundException;
import org.scoula.products.service.ProductCompareService;
import org.scoula.products.service.api.DepositApiClient;
import org.scoula.products.service.api.PensionApiClient;
import org.scoula.products.service.api.SavingApiClient;
import org.scoula.response.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 금융 상품 비교 서비스 구현체
 */
@Service
public class ProductCompareServiceImpl implements ProductCompareService {

    private final DepositApiClient depositApiClient;
    private final SavingApiClient savingApiClient;
    private final PensionApiClient pensionApiClient;

    @Autowired
    public ProductCompareServiceImpl(
            DepositApiClient depositApiClient,
            SavingApiClient savingApiClient,
            PensionApiClient pensionApiClient) {
        this.depositApiClient = depositApiClient;
        this.savingApiClient = savingApiClient;
        this.pensionApiClient = pensionApiClient;
    }

    @Override
    public ProductCompareResponse compareProducts(ProductCompareRequest request) {
        return compareProducts(request.getProductType(), request.getProductCodes());
    }

    @Override
    public ProductCompareResponse compareProducts(String productType, List<String> productIds) {
        // 각 상품 타입별 비교 로직 수행
        switch (productType.toLowerCase()) {
            case "deposit":
                return compareDepositProducts(productIds);
            case "saving":
                return compareSavingProducts(productIds);
            case "pension":
                return comparePensionProducts(productIds);
            default:
                throw new InvalidProductTypeException(ResponseCode.INVALID_PRODUCT_TYPE_ERROR);
        }
    }

    /**
     * 예금 상품들을 비교하여 비교 결과를 반환합니다.
     */
    private ProductCompareResponse compareDepositProducts(List<String> productIds) {
        // 상품 정보 조회
        List<DepositProductDTO> products = new ArrayList<>();
        for (String productId : productIds) {
            DepositProductDTO product = depositApiClient.getDepositProductDetail(productId);
            if (product == null) {
                throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
            }
            products.add(product);
        }

        // 비교 데이터 생성
        Map<String, Object> comparisonData = createDepositComparisonData(products);

        // 비교 요약 정보 생성
        ProductCompareResponse.ComparisonSummary summary = createDepositComparisonSummary(products);

        // 응답 구성
        return ProductCompareResponse.builder()
                .productType("deposit")
                .products(products)
                .comparisonData(comparisonData)
                .summary(summary)
                .build();
    }

    /**
     * 적금 상품들을 비교하여 비교 결과를 반환합니다.
     */
    private ProductCompareResponse compareSavingProducts(List<String> productIds) {
        // 상품 정보 조회
        // 임시로 예금 상품 비교 로직과 동일하게 구현 (실제로는 적금 상품에 맞게 수정 필요)
        List<DepositProductDTO> products = new ArrayList<>();
        for (String productId : productIds) {
            DepositProductDTO product = depositApiClient.getDepositProductDetail(productId);
            if (product == null) {
                throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
            }
            products.add(product);
        }

        // 비교 데이터 생성
        Map<String, Object> comparisonData = createDepositComparisonData(products);

        // 비교 요약 정보 생성
        ProductCompareResponse.ComparisonSummary summary = createDepositComparisonSummary(products);

        // 응답 구성
        return ProductCompareResponse.builder()
                .productType("saving")
                .products(products)
                .comparisonData(comparisonData)
                .summary(summary)
                .build();
    }

    /**
     * 연금 상품들을 비교하여 비교 결과를 반환합니다.
     */
    private ProductCompareResponse comparePensionProducts(List<String> productIds) {
        // 상품 정보 조회
        // 임시로 예금 상품 비교 로직과 동일하게 구현 (실제로는 연금 상품에 맞게 수정 필요)
        List<DepositProductDTO> products = new ArrayList<>();
        for (String productId : productIds) {
            DepositProductDTO product = depositApiClient.getDepositProductDetail(productId);
            if (product == null) {
                throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
            }
            products.add(product);
        }

        // 비교 데이터 생성
        Map<String, Object> comparisonData = createDepositComparisonData(products);

        // 비교 요약 정보 생성
        ProductCompareResponse.ComparisonSummary summary = createDepositComparisonSummary(products);

        // 응답 구성
        return ProductCompareResponse.builder()
                .productType("pension")
                .products(products)
                .comparisonData(comparisonData)
                .summary(summary)
                .build();
    }

    /**
     * 예금 상품들의 비교 데이터를 생성합니다.
     */
    private Map<String, Object> createDepositComparisonData(List<DepositProductDTO> products) {
        Map<String, Object> comparisonData = new HashMap<>();

        // 1. 기본 정보 비교
        Map<String, List<String>> basicInfo = new HashMap<>();
        basicInfo.put("은행명", products.stream().map(DepositProductDTO::getKorCoNm).collect(Collectors.toList()));
        basicInfo.put("상품명", products.stream().map(DepositProductDTO::getFinPrdtNm).collect(Collectors.toList()));
        basicInfo.put("상품코드", products.stream().map(DepositProductDTO::getFinPrdtCd).collect(Collectors.toList()));
        comparisonData.put("basicInfo", basicInfo);

        // 2. 금리 정보 비교
        Map<String, List<Double>> interestInfo = new HashMap<>();

        // 각 상품의 최고 금리 계산
        List<Double> maxRates = new ArrayList<>();
        List<Double> baseRates = new ArrayList<>();

        for (DepositProductDTO product : products) {
            double maxRate = 0.0;
            double baseRate = 0.0;

            if (product.getOptions() != null && !product.getOptions().isEmpty()) {
                // 기본 금리: 첫 번째 옵션의 기본 금리 사용
                baseRate = product.getOptions().get(0).getIntrRate();

                // 최고 금리: 모든 옵션 중 가장 높은 우대 금리 사용
                maxRate = product.getOptions().stream()
                        .mapToDouble(option -> option.getIntrRate2() != null ? option.getIntrRate2() : option.getIntrRate())
                        .max()
                        .orElse(0.0);
            }

            baseRates.add(baseRate);
            maxRates.add(maxRate);
        }

        interestInfo.put("기본금리(%)", baseRates);
        interestInfo.put("최고금리(%)", maxRates);
        comparisonData.put("interestInfo", interestInfo);

        // 3. 기간 정보 비교
        Map<String, List<Object>> termInfo = new HashMap<>();

        // 각 상품의 저축 기간 추출
        List<Object> terms = products.stream()
                .map(product -> {
                    if (product.getOptions() != null && !product.getOptions().isEmpty()) {
                        return product.getOptions().get(0).getSaveTrm();
                    }
                    return null;
                })
                .collect(Collectors.toList());

        termInfo.put("계약기간(개월)", terms);
        comparisonData.put("termInfo", termInfo);

        // 4. 가입 조건 비교
        Map<String, List<String>> conditionInfo = new HashMap<>();
        conditionInfo.put("가입방법", products.stream().map(DepositProductDTO::getJoinWay).collect(Collectors.toList()));
        conditionInfo.put("가입대상", products.stream().map(DepositProductDTO::getJoinMember).collect(Collectors.toList()));
        conditionInfo.put("우대조건", products.stream().map(DepositProductDTO::getSpclCnd).collect(Collectors.toList()));
        comparisonData.put("conditionInfo", conditionInfo);

        // 5. 기타 정보 비교
        Map<String, List<String>> etcInfo = new HashMap<>();
        etcInfo.put("만기 후 이자율", products.stream().map(DepositProductDTO::getMtrtInt).collect(Collectors.toList()));
        comparisonData.put("etcInfo", etcInfo);

        return comparisonData;
    }

    /**
     * 예금 상품들의 비교 요약 정보를 생성합니다.
     */
    private ProductCompareResponse.ComparisonSummary createDepositComparisonSummary(List<DepositProductDTO> products) {
        // 1. 최고 금리 상품 찾기
        DepositProductDTO highestRateProduct = null;
        double highestRate = 0.0;

        for (DepositProductDTO product : products) {
            if (product.getOptions() != null && !product.getOptions().isEmpty()) {
                double maxRate = product.getOptions().stream()
                        .mapToDouble(option -> option.getIntrRate2() != null ? option.getIntrRate2() : option.getIntrRate())
                        .max()
                        .orElse(0.0);

                if (maxRate > highestRate) {
                    highestRate = maxRate;
                    highestRateProduct = product;
                }
            }
        }

        ProductCompareResponse.ProductSummary highestRateProductSummary = null;
        if (highestRateProduct != null) {
            highestRateProductSummary = ProductCompareResponse.ProductSummary.builder()
                    .finPrdtCd(highestRateProduct.getFinPrdtCd())
                    .korCoNm(highestRateProduct.getKorCoNm())
                    .finPrdtNm(highestRateProduct.getFinPrdtNm())
                    .value(highestRate)
                    .build();
        }

        // 2. 가입 방법이 다양한 상품 찾기
        DepositProductDTO mostJoinWaysProduct = null;
        int maxJoinWays = 0;

        for (DepositProductDTO product : products) {
            String joinWay = product.getJoinWay();
            if (joinWay != null) {
                int joinWayCount = joinWay.split(",").length;
                if (joinWayCount > maxJoinWays) {
                    maxJoinWays = joinWayCount;
                    mostJoinWaysProduct = product;
                }
            }
        }

        ProductCompareResponse.ProductSummary mostJoinWaysProductSummary = null;
        if (mostJoinWaysProduct != null) {
            mostJoinWaysProductSummary = ProductCompareResponse.ProductSummary.builder()
                    .finPrdtCd(mostJoinWaysProduct.getFinPrdtCd())
                    .korCoNm(mostJoinWaysProduct.getKorCoNm())
                    .finPrdtNm(mostJoinWaysProduct.getFinPrdtNm())
                    .value(maxJoinWays)
                    .build();
        }

        // 3. 가입 대상이 넓은 상품 찾기 (예: "제한없음"이 포함된 상품)
        DepositProductDTO widestTargetProduct = null;

        for (DepositProductDTO product : products) {
            String joinMember = product.getJoinMember();
            if (joinMember != null && joinMember.contains("제한없음")) {
                widestTargetProduct = product;
                break;
            }
        }

        // 제한 없음이 없으면 첫 번째 상품 선택
        if (widestTargetProduct == null && !products.isEmpty()) {
            widestTargetProduct = products.get(0);
        }

        ProductCompareResponse.ProductSummary widestTargetProductSummary = null;
        if (widestTargetProduct != null) {
            widestTargetProductSummary = ProductCompareResponse.ProductSummary.builder()
                    .finPrdtCd(widestTargetProduct.getFinPrdtCd())
                    .korCoNm(widestTargetProduct.getKorCoNm())
                    .finPrdtNm(widestTargetProduct.getFinPrdtNm())
                    .value(widestTargetProduct.getJoinMember())
                    .build();
        }

        // 요약 정보 구성
        return ProductCompareResponse.ComparisonSummary.builder()
                .highestRateProduct(highestRateProductSummary)
                .mostJoinWaysProduct(mostJoinWaysProductSummary)
                .widestTargetProduct(widestTargetProductSummary)
                .build();
    }
}