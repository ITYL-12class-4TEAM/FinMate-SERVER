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
import org.scoula.response.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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

    // 상품 유형 열거형 정의
    public enum ProductType {
        PENSION("pension"),
        DEPOSIT("deposit");

        private final String value;

        ProductType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static ProductType fromString(String text) {
            for (ProductType type : ProductType.values()) {
                if (type.value.equalsIgnoreCase(text)) {
                    return type;
                }
            }
            return DEPOSIT; // 기본값
        }
    }

    // 상수 정의
    private static final String DEFAULT_JOIN_WAY = "인터넷뱅킹,스마트폰뱅킹,창구";
    private static final String DEFAULT_JOIN_MEMBER = "제한없음";
    private static final String DEFAULT_JOIN_DENY = "없음";
    private static final String DEFAULT_INFO_TEXT = "정보 없음";
    private static final String ISO_DATE_PATTERN = "yyyyMMdd";

    private final DepositProductMapper depositProductMapper;
    private final PensionProductMapper pensionProductMapper;
    private final PensionComparisonDataBuilder pensionComparisonDataBuilder;
    private final DepositComparisonDataBuilder depositComparisonDataBuilder;

    @Autowired
    public ProductCompareServiceImpl(
            DepositProductMapper depositProductMapper,
            PensionProductMapper pensionProductMapper,
            PensionComparisonDataBuilder pensionComparisonDataBuilder,
            DepositComparisonDataBuilder depositComparisonDataBuilder) {
        this.depositProductMapper = depositProductMapper;
        this.pensionProductMapper = pensionProductMapper;
        this.pensionComparisonDataBuilder = pensionComparisonDataBuilder;
        this.depositComparisonDataBuilder = depositComparisonDataBuilder;
    }

    @Override
    public ProductCompareResponse compareProducts(ProductCompareRequest request) {
        return compareProducts(request.getProductType(), request.getProductCodes());
    }

    @Override
    public ProductCompareResponse compareProducts(String productType, List<String> productIds) {
        // 문자열 상품 유형을 열거형으로 변환
        ProductType type = ProductType.fromString(productType);

        // Strategy 패턴 적용
        switch (type) {
            case PENSION:
                // 연금 상품 비교 (optionId는 필요 없으므로 명시적으로 null 전달)
                return comparePensionProducts(productIds, null);
            case DEPOSIT:
            default:
                // 예금 상품 비교
                return compareDepositProducts(productIds);
        }
    }

    /**
     * 예금 카테고리 상품들을 비교하여 비교 결과를 반환합니다.
     * (정기예금, 적금 등 서브카테고리 포함)
     */
    @Override
    public ProductCompareResponse compareDepositProducts(List<String> productIds) {
        // 문자열 ID 목록을 Long 타입으로 변환 (한 번에 처리)
        List<Long> longIds = convertToLongIds(productIds);

        // 한 번의 쿼리로 모든 상품 정보 조회 (N+1 문제 방지)
        List<DepositProductDTO> products = depositProductMapper.findByProductIds(longIds);

        if (products.isEmpty()) {
            throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
        }

        // 모든 상품 ID 추출
        List<Long> fetchedProductIds = products.stream()
                .map(DepositProductDTO::getProductId)
                .collect(Collectors.toList());

        // 한 번의 쿼리로 모든 상품의 옵션 정보 조회 (N+1 문제 방지)
        List<DepositOptionDTO> allOptions = depositProductMapper.findOptionsByProductIds(fetchedProductIds);

        // 상품 ID별로 옵션 그룹화
        Map<Long, List<DepositOptionDTO>> optionsByProductId = allOptions.stream()
                .collect(Collectors.groupingBy(DepositOptionDTO::getProductId));

        // 각 상품에 해당하는 옵션 설정 및 데이터 보강
        List<DepositProductDTO> enrichedProducts = products.stream()
                .map(product -> {
                    List<DepositOptionDTO> options = optionsByProductId.getOrDefault(product.getProductId(), new ArrayList<>());
                    product.setOptions(options);
                    return enrichDepositProductData(product);
                })
                .collect(Collectors.toList());

        // 비교 데이터 생성
        Map<String, Object> comparisonData = depositComparisonDataBuilder.build(enrichedProducts);

        // 비교 요약 정보 생성
        ProductCompareResponse.ComparisonSummary summary = createDepositComparisonSummary(enrichedProducts);

        // 응답 구성
        return ProductCompareResponse.builder()
                .productType(ProductType.DEPOSIT.getValue())
                .products(enrichedProducts)
                .comparisonData(comparisonData)
                .summary(summary)
                .build();
    }

    /**
     * 예금 상품 데이터를 보강하는 메서드
     */
    private DepositProductDTO enrichDepositProductData(DepositProductDTO original) {
        // from() 대신 toBuilder() 사용
        return original.toBuilder()
                .joinWay(Optional.ofNullable(original.getJoinWay()).orElse(DEFAULT_JOIN_WAY))
                .joinMember(Optional.ofNullable(original.getJoinMember()).orElse(DEFAULT_JOIN_MEMBER))
                .joinDeny(Optional.ofNullable(original.getJoinDeny()).orElse(DEFAULT_JOIN_DENY))
                .dclsStrtDay(formatDateIfNeeded(original.getDclsStrtDay()))
                .build();
    }

    /**
     * 연금 상품들을 비교하여 비교 결과를 반환합니다. (Long 타입 ID 목록)
     */
    @Override
    public ProductCompareResponse comparePensionProductsLong(List<Long> productIds) {
        // 한 번의 쿼리로 모든 상품 정보 조회 (N+1 문제 방지)
        List<PensionProductDTO> products = pensionProductMapper.findByProductIds(productIds);

        if (products.isEmpty()) {
            throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
        }

        // 모든 상품 ID 추출
        List<Long> fetchedProductIds = products.stream()
                .map(PensionProductDTO::getProductId)
                .collect(Collectors.toList());

        // 한 번의 쿼리로 모든 상품의 옵션 정보 조회 (N+1 문제 방지)
        List<PensionOptionDTO> allOptions = pensionProductMapper.findOptionsByProductIds(fetchedProductIds);

        // 상품 ID별로 옵션 그룹화
        Map<Long, List<PensionOptionDTO>> optionsByProductId = allOptions.stream()
                .collect(Collectors.groupingBy(PensionOptionDTO::getProductId));

        // 각 상품에 해당하는 옵션 설정 및 데이터 보강
        List<PensionProductDTO> enrichedProducts = products.stream()
                .map(product -> {
                    List<PensionOptionDTO> options = optionsByProductId.getOrDefault(product.getProductId(), new ArrayList<>());
                    product.setOptions(options);
                    return enrichPensionProductData(product);
                })
                .collect(Collectors.toList());

        // 비교 데이터 생성
        Map<String, Object> comparisonData = pensionComparisonDataBuilder.build(enrichedProducts);

        // 요약 정보 생성
        ProductCompareResponse.ComparisonSummary summary = createPensionComparisonSummary(enrichedProducts);

        // 응답 구성
        return ProductCompareResponse.builder()
                .productType(ProductType.PENSION.getValue())
                .products(enrichedProducts)
                .comparisonData(comparisonData)
                .summary(summary)
                .build();
    }

    @Override
    public ProductCompareResponse comparePensionProducts(List<String> productIds, String optionId) {
        // 문자열 ID 목록을 Long 타입으로 변환 (한 번에 처리)
        List<Long> longIds = convertToLongIds(productIds);

        // Long 타입 ID 목록으로 상품 비교 결과 조회
        return comparePensionProductsLong(longIds);
    }

    /**
     * 연금 상품 데이터를 보강하는 메서드
     */
    private PensionProductDTO enrichPensionProductData(PensionProductDTO product) {
        // from() 대신 toBuilder() 사용
        PensionProductDTO.PensionProductDTOBuilder builder = product.toBuilder()
                .joinWay(Optional.ofNullable(product.getJoinWay()).orElse(DEFAULT_INFO_TEXT))
                .pnsnRcvMthd(Optional.ofNullable(product.getPnsnRcvMthd()).orElse(DEFAULT_INFO_TEXT))
                .dclsStrtDay(formatDateIfNeeded(product.getDclsStrtDay()));

        if (product.getOptions() != null && !product.getOptions().isEmpty()) {
            // 최소/최대 가입 연령 계산
            List<Integer> entryAges = product.getOptions().stream()
                    .map(PensionOptionDTO::getPnsnEntrAge)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (!entryAges.isEmpty()) {
                builder.minEntryAge(Collections.min(entryAges))
                        .maxEntryAge(Collections.max(entryAges));
            }

            // 최소/최대 납입금액 계산
            List<Integer> payments = product.getOptions().stream()
                    .map(PensionOptionDTO::getMonPaymAtm)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (!payments.isEmpty()) {
                builder.minPayment(Collections.min(payments))
                        .maxPayment(Collections.max(payments));
            }

            // 옵션에서 월 납입액을 대표값으로 설정 (첫 번째 옵션 사용)
            if (product.getOptions().get(0).getMonPaymAtm() != null) {
                builder.mnthPymAtm(product.getOptions().get(0).getMonPaymAtm().longValue());
            }

            // 연금 시작 연령 설정
            if (product.getOptions().get(0).getPnsnStrtAge() != null) {
                builder.pnsnStrtAge(product.getOptions().get(0).getPnsnStrtAge());
            }
        }

        return builder.build();
    }

    /**
     * 문자열 ID 목록을 Long 타입으로 변환
     */
    private List<Long> convertToLongIds(List<String> productIds) {
        return productIds.stream()
                .map(id -> {
                    try {
                        return Long.parseLong(id);
                    } catch (NumberFormatException e) {
                        throw new InvalidProductTypeException(ResponseCode.INVALID_PRODUCT_TYPE_ERROR);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 날짜 문자열을 ISO 형식으로 변환
     */
    private String formatDateIfNeeded(String dateStr) {
        if (dateStr != null && !dateStr.contains("-")) {
            try {
                LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(ISO_DATE_PATTERN));
                return date.format(DateTimeFormatter.ISO_DATE);
            } catch (Exception e) {
                // 날짜 형식 변환 실패 처리
            }
        }
        return dateStr;
    }

    /**
     * 연금 상품들의 비교 요약 정보 생성
     */
    /**
     * 연금저축 상품들의 비교 요약 정보 생성
     */
    private ProductCompareResponse.ComparisonSummary createPensionComparisonSummary(List<PensionProductDTO> products) {
        return ProductCompareResponse.ComparisonSummary.builder()
                .highestRateProduct(findHighestProfitProduct(products)) // 수익률이 가장 높은 상품
                .mostJoinWaysProduct(findHighestGuarRateProduct(products)) // 보증이율이 가장 높은 상품
                .widestTargetProduct(findWidestAgeRangeProduct(products)) // 가입 연령 폭이 가장 넓은 상품
                .earliestStartAgeProduct(findEarliestStartAgeProduct(products)) // 연금 수령 시작 나이가 가장 이른 상품
                .build();
    }

    /**
     * 수익률이 가장 높은 연금저축 상품
     */
    private ProductCompareResponse.ProductSummary findHighestProfitProduct(List<PensionProductDTO> products) {
        return products.stream()
                .max(Comparator.comparing(p -> Optional.ofNullable(p.getProfitRate1()).orElse(0.0)))
                .map(p -> toProductSummaryWithValue(p, p.getProfitRate1()))
                .orElse(null);
    }

    /**
     * 보증이율이 가장 높은 연금저축 상품
     */
    private ProductCompareResponse.ProductSummary findHighestGuarRateProduct(List<PensionProductDTO> products) {
        return products.stream()
                .max(Comparator.comparing(p -> Optional.ofNullable(p.getGuarRate()).orElse(0.0)))
                .map(p -> toProductSummaryWithValue(p, p.getGuarRate()))
                .orElse(null);
    }

    /**
     * 가입 가능 연령대가 가장 넓은 연금저축 상품
     */
    private ProductCompareResponse.ProductSummary findWidestAgeRangeProduct(List<PensionProductDTO> products) {
        return products.stream()
                .filter(p -> p.getMinEntryAge() != null && p.getMaxEntryAge() != null)
                .max(Comparator.comparing(p -> p.getMaxEntryAge() - p.getMinEntryAge()))
                .map(p -> toProductSummaryWithText(p, "만 " + p.getMinEntryAge() + "세~" + p.getMaxEntryAge() + "세"))
                .orElse(null);
    }

    /**
     * 연금 수령 시작 나이가 가장 빠른 연금저축 상품
     */
    private ProductCompareResponse.ProductSummary findEarliestStartAgeProduct(List<PensionProductDTO> products) {
        return products.stream()
                .filter(p -> p.getOptions() != null && !p.getOptions().isEmpty())
                .flatMap(p -> p.getOptions().stream()
                        .filter(o -> o.getPnsnStrtAge() != null)
                        .map(o -> Map.entry(p, o.getPnsnStrtAge())))
                .min(Map.Entry.comparingByValue())
                .map(entry -> toProductSummaryWithText(entry.getKey(), "만 " + entry.getValue() + "세"))
                .orElse(null);
    }

    /**
     * 수치 정보를 포함하는 상품 요약 정보 생성
     */
    private ProductCompareResponse.ProductSummary toProductSummaryWithValue(PensionProductDTO product, Double value) {
        return ProductCompareResponse.ProductSummary.builder()
                .finPrdtCd(product.getFinPrdtCd())
                .korCoNm(product.getKorCoNm())
                .finPrdtNm(product.getFinPrdtNm())
                .value(value != null ? value : 0.0)
                .build();
    }

    /**
     * 텍스트 정보를 포함하는 상품 요약 정보 생성
     */
    private ProductCompareResponse.ProductSummary toProductSummaryWithText(PensionProductDTO product, String text) {
        return ProductCompareResponse.ProductSummary.builder()
                .finPrdtCd(product.getFinPrdtCd())
                .korCoNm(product.getKorCoNm())
                .finPrdtNm(product.getFinPrdtNm())
                .value(text)
                .build();
    }

    /**
     * 예금 상품들의 비교 요약 정보 생성
     */
    private ProductCompareResponse.ComparisonSummary createDepositComparisonSummary(List<DepositProductDTO> products) {
        return ProductCompareResponse.ComparisonSummary.builder()
                .highestRateProduct(findHighestInterestRateProduct(products)) // 금리가 가장 높은 상품
                .mostJoinWaysProduct(findMostJoinWaysProduct(products)) // 가입 경로가 가장 다양한 상품
                .widestTargetProduct(findWidestTargetDepositProduct(products)) // 가입 대상이 가장 넓은 상품
                .build();
    }

    /**
     * 최고 우대금리를 가진 예금 상품
     */
    private ProductCompareResponse.ProductSummary findHighestInterestRateProduct(List<DepositProductDTO> products) {
        return products.stream()
                .filter(p -> p.getOptions() != null && !p.getOptions().isEmpty())
                .flatMap(p -> p.getOptions().stream()
                        .map(o -> Map.entry(p, o.getIntrRate2() != null ? o.getIntrRate2() : o.getIntrRate())))
                .max(Map.Entry.comparingByValue())
                .map(entry -> ProductCompareResponse.ProductSummary.builder()
                        .finPrdtCd(entry.getKey().getFinPrdtCd())
                        .korCoNm(entry.getKey().getKorCoNm())
                        .finPrdtNm(entry.getKey().getFinPrdtNm())
                        .value(entry.getValue())
                        .build())
                .orElse(null);
    }

    /**
     * 가입 방법이 가장 다양한 예금 상품
     */
    private ProductCompareResponse.ProductSummary findMostJoinWaysProduct(List<DepositProductDTO> products) {
        return products.stream()
                .filter(p -> p.getJoinWay() != null)
                .map(p -> Map.entry(p, p.getJoinWay().split(",").length))
                .max(Map.Entry.comparingByValue())
                .map(entry -> ProductCompareResponse.ProductSummary.builder()
                        .finPrdtCd(entry.getKey().getFinPrdtCd())
                        .korCoNm(entry.getKey().getKorCoNm())
                        .finPrdtNm(entry.getKey().getFinPrdtNm())
                        .value(entry.getValue())
                        .build())
                .orElse(null);
    }

    /**
     * 가입 대상이 가장 넓은 예금 상품 ("제한없음" 우선)
     */
    private ProductCompareResponse.ProductSummary findWidestTargetDepositProduct(List<DepositProductDTO> products) {
        Optional<DepositProductDTO> noLimitProduct = products.stream()
                .filter(p -> p.getJoinMember() != null && p.getJoinMember().contains(DEFAULT_JOIN_MEMBER))
                .findFirst();

        if (noLimitProduct.isPresent()) {
            DepositProductDTO product = noLimitProduct.get();
            return ProductCompareResponse.ProductSummary.builder()
                    .finPrdtCd(product.getFinPrdtCd())
                    .korCoNm(product.getKorCoNm())
                    .finPrdtNm(product.getFinPrdtNm())
                    .value(product.getJoinMember())
                    .build();
        }

        // "제한없음"이 없는 경우, 첫 번째 상품을 반환
        if (!products.isEmpty()) {
            DepositProductDTO product = products.get(0);
            return ProductCompareResponse.ProductSummary.builder()
                    .finPrdtCd(product.getFinPrdtCd())
                    .korCoNm(product.getKorCoNm())
                    .finPrdtNm(product.getFinPrdtNm())
                    .value(product.getJoinMember())
                    .build();
        }

        return null;
    }

}