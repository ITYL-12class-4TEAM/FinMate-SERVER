package org.scoula.products.service.impl;

import org.scoula.products.dto.request.ProductCompareRequest;
import org.scoula.products.dto.response.ProductCompareResponse;
import org.scoula.products.dto.response.deposit.DepositOptionDTO;
import org.scoula.products.dto.response.deposit.DepositProductDTO;
import org.scoula.products.dto.response.pension.PensionOptionDTO;
import org.scoula.products.dto.response.pension.PensionProductDTO;
import org.scoula.products.exception.InvalidProductTypeException;
import org.scoula.products.exception.ProductNotFoundException;
import org.scoula.products.mapper.DepositProductMapper;
import org.scoula.products.mapper.PensionProductMapper;
import org.scoula.products.service.ProductCompareService;
import org.scoula.products.service.api.DepositApiClient;
import org.scoula.products.service.api.PensionApiClient;
import org.scoula.response.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 금융 상품 비교 서비스 구현체
 */
@Service
public class ProductCompareServiceImpl implements ProductCompareService {

    private final DepositProductMapper depositProductMapper;
    private final PensionProductMapper pensionProductMapper;

    @Autowired
    public ProductCompareServiceImpl(
            DepositProductMapper depositProductMapper,
            PensionProductMapper pensionProductMapper) {
        this.depositProductMapper = depositProductMapper;
        this.pensionProductMapper = pensionProductMapper;
    }

    @Override
    public ProductCompareResponse compareProducts(ProductCompareRequest request) {
        return compareProducts(request.getProductType(), request.getProductCodes());
    }

    @Override
    public ProductCompareResponse compareProducts(String productType, List<String> productIds) {
        // 상품 유형별 비교 로직 수행 - 카테고리 기준으로 분기
        if ("pension".equalsIgnoreCase(productType)) {
            // 연금 상품 비교
            return comparePensionProducts(productIds, null);
        } else {
            // deposit 카테고리 (정기예금, 적금 등 모든 서브카테고리 포함)
            return compareDepositProducts(productIds);
        }
    }

    /**
     * 예금 카테고리 상품들을 비교하여 비교 결과를 반환합니다.
     * (정기예금, 적금 등 서브카테고리 포함)
     */
    @Override
    public ProductCompareResponse compareDepositProducts(List<String> productIds) {
        // 상품 정보 조회
        List<DepositProductDTO> products = new ArrayList<>();
        for (String productId : productIds) {
            // 문자열 ID를 Long으로 변환
            Long id;
            try {
                id = Long.parseLong(productId);
            } catch (NumberFormatException e) {
                throw new InvalidProductTypeException(ResponseCode.INVALID_PRODUCT_TYPE_ERROR);
            }

            // Mapper를 통해 상품 정보 조회
            DepositProductDTO product = depositProductMapper.findByProductId(id);
            if (product == null) {
                throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
            }

            // 옵션 정보 조회 및 설정
            List<DepositOptionDTO> options = depositProductMapper.findOptionsByProductId(id);
            product.setOptions(options);

            // 데이터 보강 (필요한 경우, pension과 유사하게)
            enrichDepositProductData(product);
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
     * 예금 상품 데이터를 보강하는 메서드
     */
    private void enrichDepositProductData(DepositProductDTO product) {
        // null인 필드에 기본값 설정
        if (product.getJoinWay() == null) {
            product.setJoinWay("인터넷뱅킹,스마트폰뱅킹,창구");
        }

        if (product.getJoinMember() == null) {
            product.setJoinMember("제한없음");
        }

        if (product.getJoinDeny() == null) {
            product.setJoinDeny("없음");
        }

        // 날짜 형식 변환 (필요한 경우)
        if (product.getDclsStrtDay() != null && !product.getDclsStrtDay().contains("-")) {
            try {
                LocalDate date = LocalDate.parse(product.getDclsStrtDay(), DateTimeFormatter.ofPattern("yyyyMMdd"));
                product.setDclsStrtDay(date.format(DateTimeFormatter.ISO_DATE));
            } catch (Exception e) {
                // 날짜 형식 변환 실패 처리
            }
        }
    }

    /**
     * 연금 상품들을 비교하여 비교 결과를 반환합니다. (Long 타입 ID 목록)
     */
    // ProductCompareServiceImpl.java에서 메서드 시그니처 수정
    @Override
    public ProductCompareResponse comparePensionProductsLong(List<Long> productIds) {
        // 상품 정보 조회
        List<PensionProductDTO> products = new ArrayList<>();
        for (Long productId : productIds) {
            // PensionProductMapper를 사용하여 연금 상품 조회
            PensionProductDTO product = pensionProductMapper.findByProductId(productId);
            if (product == null) {
                throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
            }

            // 옵션 정보 조회 및 설정
            List<PensionOptionDTO> options = pensionProductMapper.findOptionsByProductId(productId);
            product.setOptions(options);

            products.add(product);
        }

        // 비교 데이터 생성
        Map<String, Object> comparisonData = createPensionComparisonData(products);

        // 요약 정보 생성
        ProductCompareResponse.ComparisonSummary summary = createPensionComparisonSummary(products);

        // 응답 구성
        return ProductCompareResponse.builder()
                .productType("pension")
                .products(products)
                .comparisonData(comparisonData)
                .summary(summary)
                .build();
    }

    @Override
    public ProductCompareResponse comparePensionProducts(List<String> productIds , String optionId) {
        List<Long> longIds = productIds.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

        // 옵션 ID를 Long으로 변환 (null인 경우 처리)
        Long optionIdLong = null;
        if (optionId != null && !optionId.isEmpty()) {
            try {
                optionIdLong = Long.parseLong(optionId);
            } catch (NumberFormatException e) {
                // 유효하지 않은 옵션 ID 처리 (필요한 경우)
            }
        }

        // 상품 정보 조회
        List<PensionProductDTO> products = new ArrayList<>();
        for (Long productId : longIds) {
            // PensionProductMapper를 사용하여 연금 상품 조회
            PensionProductDTO product = pensionProductMapper.findByProductId(productId);
            if (product == null) {
                throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
            }

            // 옵션 정보 조회 및 설정
            List<PensionOptionDTO> options = pensionProductMapper.findOptionsByProductId(productId);
            product.setOptions(options);

            // 데이터 보강 로직 추가
            enrichPensionProductData(product);

            products.add(product);
        }

        // 비교 데이터 생성
        Map<String, Object> comparisonData = createPensionComparisonData(products);

        // 요약 정보 생성
        ProductCompareResponse.ComparisonSummary summary = createPensionComparisonSummary(products);

        // 응답 구성
        return ProductCompareResponse.builder()
                .productType("pension")
                .products(products)
                .comparisonData(comparisonData)
                .summary(summary)
                .build();
    }

    /**
     * 연금 상품 데이터를 보강하는 메서드
     */
    private void enrichPensionProductData(PensionProductDTO product) {
        if (product.getOptions() != null && !product.getOptions().isEmpty()) {
            // 최소/최대 가입 연령 계산
            List<Integer> entryAges = product.getOptions().stream()
                    .map(PensionOptionDTO::getPnsnEntrAge)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (!entryAges.isEmpty()) {
                product.setMinEntryAge(Collections.min(entryAges));
                product.setMaxEntryAge(Collections.max(entryAges));
            }

            // 최소/최대 납입금액 계산
            List<Integer> payments = product.getOptions().stream()
                    .map(PensionOptionDTO::getMonPaymAtm)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (!payments.isEmpty()) {
                product.setMinPayment(Collections.min(payments));
                product.setMaxPayment(Collections.max(payments));
            }

            // 옵션에서 월 납입액을 대표값으로 설정 (첫 번째 옵션 사용)
            if (product.getOptions().get(0).getMonPaymAtm() != null) {
                product.setMnthPymAtm(product.getOptions().get(0).getMonPaymAtm().longValue());
            }

            // 연금 시작 연령 설정
            if (product.getOptions().get(0).getPnsnStrtAge() != null) {
                product.setPnsnStrtAge(product.getOptions().get(0).getPnsnStrtAge());
            }
        }

        // null인 필드에 기본값 설정
        if (product.getJoinWay() == null) {
            product.setJoinWay("정보 없음");
        }

        if (product.getPnsnRcvMthd() == null) {
            product.setPnsnRcvMthd("정보 없음");
        }

        // 날짜 형식 변환 (필요한 경우)
        if (product.getDclsStrtDay() != null && !product.getDclsStrtDay().contains("-")) {
            try {
                LocalDate date = LocalDate.parse(product.getDclsStrtDay(), DateTimeFormatter.ofPattern("yyyyMMdd"));
                product.setDclsStrtDay(date.format(DateTimeFormatter.ISO_DATE));
            } catch (Exception e) {
                // 날짜 형식 변환 실패 처리
            }
        }
    }

    /**
     * 연금 상품들의 비교 데이터를 생성합니다.
     */
    private Map<String, Object> createPensionComparisonData(List<PensionProductDTO> products) {
        Map<String, Object> comparisonData = new HashMap<>();

        // 1. 기본 정보 비교
        Map<String, List<String>> basicInfo = new HashMap<>();
        basicInfo.put("금융회사", products.stream().map(PensionProductDTO::getKorCoNm).collect(Collectors.toList()));
        basicInfo.put("상품명", products.stream().map(PensionProductDTO::getFinPrdtNm).collect(Collectors.toList()));
        basicInfo.put("연금종류", products.stream().map(PensionProductDTO::getPnsnKindNm).collect(Collectors.toList()));
        basicInfo.put("상품유형", products.stream().map(PensionProductDTO::getPrdtTypeNm).collect(Collectors.toList()));
        basicInfo.put("위험등급", products.stream().map(PensionProductDTO::getCategory).collect(Collectors.toList()));
        comparisonData.put("basicInfo", basicInfo);

        // 2. 수익률 정보 비교
        Map<String, List<Double>> rateInfo = new HashMap<>();
        rateInfo.put("최저보증이율(%)", products.stream().map(p -> p.getGuarRate() != null ? p.getGuarRate() : 0.0).collect(Collectors.toList()));
        rateInfo.put("1년 수익률(%)", products.stream().map(p -> p.getProfitRate1() != null ? p.getProfitRate1() : 0.0).collect(Collectors.toList()));
        rateInfo.put("2년 수익률(%)", products.stream().map(p -> p.getProfitRate2() != null ? p.getProfitRate2() : 0.0).collect(Collectors.toList()));
        rateInfo.put("3년 수익률(%)", products.stream().map(p -> p.getProfitRate3() != null ? p.getProfitRate3() : 0.0).collect(Collectors.toList()));
        comparisonData.put("rateInfo", rateInfo);

        // 3. 가입 조건 비교
        Map<String, List<String>> conditionInfo = new HashMap<>();

        // 옵션 정보에서 가입 연령 범위 추출
        List<String> entryAgeRanges = products.stream()
                .map(product -> {
                    if (product.getOptions() != null && !product.getOptions().isEmpty()) {
                        // 가입 연령 범위 계산
                        Integer minAge = product.getOptions().stream()
                                .map(PensionOptionDTO::getPnsnEntrAge)
                                .filter(Objects::nonNull)
                                .min(Integer::compare)
                                .orElse(null);

                        Integer maxAge = product.getOptions().stream()
                                .map(PensionOptionDTO::getPnsnEntrAge)
                                .filter(Objects::nonNull)
                                .max(Integer::compare)
                                .orElse(null);

                        return (minAge != null && maxAge != null) ? "만 " + minAge + "세~" : "정보 없음";
                    }
                    return "정보 없음";
                })
                .collect(Collectors.toList());

        // 수령 시작 나이 정보 추가
        List<String> pensionStartAges = products.stream()
                .map(product -> {
                    if (product.getOptions() != null && !product.getOptions().isEmpty()) {
                        // 수령 시작 나이 추출
                        Integer startAge = product.getOptions().stream()
                                .map(PensionOptionDTO::getPnsnStrtAge)
                                .filter(Objects::nonNull)
                                .findFirst()
                                .orElse(null);

                        return (startAge != null) ? "만 " + startAge + "세~" : "정보 없음";
                    }
                    return "정보 없음";
                })
                .collect(Collectors.toList());

        // 가입기간(납입기간) (추가)
        List<String> paymentPeriods = products.stream()
                .map(product -> {
                    if (product.getOptions() != null && !product.getOptions().isEmpty()) {
                        // 납입 기간 추출 (paym_prd 필드 사용)
                        List<Integer> periods = product.getOptions().stream()
                                .map(PensionOptionDTO::getPaymPrd)
                                .filter(Objects::nonNull)
                                .distinct()
                                .sorted()
                                .collect(Collectors.toList());

                        if (!periods.isEmpty()) {
                            return periods + "년";
                        }

                    }
                    return "정보 없음";
                })
                .collect(Collectors.toList());

        // 옵션 정보에서 납입금액 범위 추출
        List<String> paymentRanges = products.stream()
                .map(product -> {
                    if (product.getOptions() != null && !product.getOptions().isEmpty()) {
                        // 납입금액 범위 계산
                        Integer monPayment = product.getOptions().stream()
                                .map(PensionOptionDTO::getMonPaymAtm)
                                .filter(Objects::nonNull)
                                .min(Integer::compare)
                                .orElse(null);

//                        Integer maxPayment = product.getOptions().stream()
//                                .map(PensionOptionDTO::getMonPaymAtm)
//                                .filter(Objects::nonNull)
//                                .max(Integer::compare)
//                                .orElse(null);

                        return (monPayment != null)
                                ? String.format("%,d만원", monPayment)
                                : "정보 없음";
                    }
                    return "정보 없음";
                })
                .collect(Collectors.toList());

        conditionInfo.put("가입연령", entryAgeRanges);
        conditionInfo.put("수령시작나이", pensionStartAges);
        conditionInfo.put("납입기간", paymentPeriods);
        conditionInfo.put("월납입금액", paymentRanges);

        // 가입 방법 정보 추가 (있는 경우)
        if (products.stream().anyMatch(p -> p.getJoinWay() != null)) {
            conditionInfo.put("가입방법", products.stream()
                    .map(p -> p.getJoinWay() != null ? p.getJoinWay() : "정보 없음")
                    .collect(Collectors.toList()));
        }

        comparisonData.put("conditionInfo", conditionInfo);

        // 4. 수수료 정보 비교 (새로 추가)
        Map<String, List<String>> feeInfo = new HashMap<>();
        // 수수료 관련 데이터가 있다면 추가
        // 예: 가입 수수료, 유지 수수료, 환매 수수료 등

        // 5. 세제 혜택 정보 (새로 추가)
        Map<String, List<String>> taxInfo = new HashMap<>();
        // 세제 혜택 관련 데이터가 있다면 추가

        return comparisonData;
    }

    /**
     * 연금 상품들의 비교 요약 정보를 생성합니다.
     */
    private ProductCompareResponse.ComparisonSummary createPensionComparisonSummary(List<PensionProductDTO> products) {
        // 1. 최고 수익률 상품 찾기
        PensionProductDTO highestProfitProduct = null;
        double highestProfit = 0.0;

        for (PensionProductDTO product : products) {
            double profit = product.getProfitRate1() != null ? product.getProfitRate1() : 0.0;
            if (profit > highestProfit) {
                highestProfit = profit;
                highestProfitProduct = product;
            }
        }

        ProductCompareResponse.ProductSummary highestProfitProductSummary = null;
        if (highestProfitProduct != null) {
            highestProfitProductSummary = ProductCompareResponse.ProductSummary.builder()
                    .finPrdtCd(highestProfitProduct.getFinPrdtCd())
                    .korCoNm(highestProfitProduct.getKorCoNm())
                    .finPrdtNm(highestProfitProduct.getFinPrdtNm())
                    .value(highestProfit)
                    .build();
        }

        // 2. 최저 보증이율이 가장 높은 상품 찾기
        PensionProductDTO highestGuarRateProduct = null;
        double highestGuarRate = 0.0;

        for (PensionProductDTO product : products) {
            double guarRate = product.getGuarRate() != null ? product.getGuarRate() : 0.0;
            if (guarRate > highestGuarRate) {
                highestGuarRate = guarRate;
                highestGuarRateProduct = product;
            }
        }

        ProductCompareResponse.ProductSummary highestGuarRateProductSummary = null;
        if (highestGuarRateProduct != null) {
            highestGuarRateProductSummary = ProductCompareResponse.ProductSummary.builder()
                    .finPrdtCd(highestGuarRateProduct.getFinPrdtCd())
                    .korCoNm(highestGuarRateProduct.getKorCoNm())
                    .finPrdtNm(highestGuarRateProduct.getFinPrdtNm())
                    .value(highestGuarRate)
                    .build();
        }

        // 3. 가입 연령 범위가 가장 넓은 상품 찾기
        PensionProductDTO widestAgeRangeProduct = null;
        int widestAgeRange = 0;

        for (PensionProductDTO product : products) {
            Integer minAge = product.getMinEntryAge();
            Integer maxAge = product.getMaxEntryAge();

            if (minAge != null && maxAge != null) {
                int ageRange = maxAge - minAge;
                if (ageRange > widestAgeRange) {
                    widestAgeRange = ageRange;
                    widestAgeRangeProduct = product;
                }
            }
        }

        ProductCompareResponse.ProductSummary widestAgeRangeProductSummary = null;
        if (widestAgeRangeProduct != null) {
            widestAgeRangeProductSummary = ProductCompareResponse.ProductSummary.builder()
                    .finPrdtCd(widestAgeRangeProduct.getFinPrdtCd())
                    .korCoNm(widestAgeRangeProduct.getKorCoNm())
                    .finPrdtNm(widestAgeRangeProduct.getFinPrdtNm())
                    .value("만 " + widestAgeRangeProduct.getMinEntryAge() + "세~")
                    .build();
        }

        // 수령 시작 나이가 가장 빠른 상품 찾기
        PensionProductDTO earliestStartAgeProduct = null;
        int earliestStartAge = Integer.MAX_VALUE;

        for (PensionProductDTO product : products) {
            if (product.getOptions() != null && !product.getOptions().isEmpty()) {
                OptionalInt minStartAge = product.getOptions().stream()
                        .map(PensionOptionDTO::getPnsnStrtAge)
                        .filter(Objects::nonNull)
                        .mapToInt(Integer::intValue)
                        .min();

                if (minStartAge.isPresent() && minStartAge.getAsInt() < earliestStartAge) {
                    earliestStartAge = minStartAge.getAsInt();
                    earliestStartAgeProduct = product;
                }
            }
        }

        ProductCompareResponse.ProductSummary earliestStartAgeProductSummary = null;
        if (earliestStartAgeProduct != null) {
            earliestStartAgeProductSummary = ProductCompareResponse.ProductSummary.builder()
                    .finPrdtCd(earliestStartAgeProduct.getFinPrdtCd())
                    .korCoNm(earliestStartAgeProduct.getKorCoNm())
                    .finPrdtNm(earliestStartAgeProduct.getFinPrdtNm())
                    .value("만 " + earliestStartAge + "세")
                    .build();
        }

        // 요약 정보 구성
        return ProductCompareResponse.ComparisonSummary.builder()
                .highestRateProduct(highestProfitProductSummary) // 수익률 높은 상품
                .mostJoinWaysProduct(highestGuarRateProductSummary) // 보증이율 높은 상품 (기존 필드 재활용)
                .widestTargetProduct(widestAgeRangeProductSummary) // 가입 연령 범위 넓은 상품
                .earliestStartAgeProduct(earliestStartAgeProductSummary) // 수령 시작 나이가 가장 빠른 상품
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