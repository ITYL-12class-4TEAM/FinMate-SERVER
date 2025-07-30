package org.scoula.products.service.impl;

import org.scoula.products.dto.response.pension.PensionOptionDTO;
import org.scoula.products.dto.response.pension.PensionProductDTO;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class PensionComparisonDataBuilder {

    public enum ComparisonDataKey {
        BASIC_INFO("basicInfo"),
        RATE_INFO("rateInfo"),
        CONDITION_INFO("conditionInfo");

        private final String key;

        ComparisonDataKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    public Map<String, Object> build(List<PensionProductDTO> products) {
        return Map.of(
            ComparisonDataKey.BASIC_INFO.getKey(), buildBasicInfo(products),
            ComparisonDataKey.RATE_INFO.getKey(), buildRateInfo(products),
            ComparisonDataKey.CONDITION_INFO.getKey(), buildConditionInfo(products)
        );
    }

    private Map<String, List<String>> buildBasicInfo(List<PensionProductDTO> products) {
        Map<String, List<String>> basicInfo = new HashMap<>();
        basicInfo.put("금융회사", products.stream().map(PensionProductDTO::getKorCoNm).collect(Collectors.toList()));
        basicInfo.put("상품명", products.stream().map(PensionProductDTO::getFinPrdtNm).collect(Collectors.toList()));
        basicInfo.put("연금종류", products.stream().map(PensionProductDTO::getPnsnKindNm).collect(Collectors.toList()));
        basicInfo.put("상품유형", products.stream().map(PensionProductDTO::getPrdtTypeNm).collect(Collectors.toList()));
        basicInfo.put("위험등급", products.stream().map(PensionProductDTO::getCategory).collect(Collectors.toList()));
        return basicInfo;
    }

    private Map<String, List<Double>> buildRateInfo(List<PensionProductDTO> products) {
        Map<String, List<Double>> rateInfo = new HashMap<>();
        rateInfo.put("최저보증이율(%)", products.stream()
            .map(p -> p.getGuarRate() != null ? p.getGuarRate() : 0.0)
            .collect(Collectors.toList()));
        rateInfo.put("1년 수익률(%)", products.stream()
            .map(p -> p.getProfitRate1() != null ? p.getProfitRate1() : 0.0)
            .collect(Collectors.toList()));
        rateInfo.put("2년 수익률(%)", products.stream()
            .map(p -> p.getProfitRate2() != null ? p.getProfitRate2() : 0.0)
            .collect(Collectors.toList()));
        rateInfo.put("3년 수익률(%)", products.stream()
            .map(p -> p.getProfitRate3() != null ? p.getProfitRate3() : 0.0)
            .collect(Collectors.toList()));
        return rateInfo;
    }

    private Map<String, List<String>> buildConditionInfo(List<PensionProductDTO> products) {
        Map<String, List<String>> conditionInfo = new HashMap<>();

        // 가입 연령 범위
        conditionInfo.put("가입연령", extractEntryAgeRanges(products));

        // 수령 시작 나이
        conditionInfo.put("수령시작나이", extractPensionStartAges(products));

        // 납입 기간
        conditionInfo.put("납입기간", extractPaymentPeriods(products));

        // 월 납입 금액
        conditionInfo.put("월납입금액", extractMonthlyPayments(products));

        // 가입 방법
        if (products.stream().anyMatch(p -> p.getJoinWay() != null)) {
            conditionInfo.put("가입방법", products.stream()
                .map(p -> p.getJoinWay() != null ? p.getJoinWay() : "정보 없음")
                .collect(Collectors.toList()));
        }

        return conditionInfo;
    }

    private List<String> extractEntryAgeRanges(List<PensionProductDTO> products) {
        return products.stream()
            .map(product -> {
                if (product.getOptions() != null && !product.getOptions().isEmpty()) {
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

                    return (minAge != null && maxAge != null) ? "만 " + minAge + "세~" + maxAge + "세" : "정보 없음";
                }
                return "정보 없음";
            })
            .collect(Collectors.toList());
    }

    private List<String> extractPensionStartAges(List<PensionProductDTO> products) {
        return products.stream()
            .map(product -> {
                if (product.getOptions() != null && !product.getOptions().isEmpty()) {
                    Integer startAge = product.getOptions().stream()
                        .map(PensionOptionDTO::getPnsnStrtAge)
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(null);

                    return (startAge != null) ? "만 " + startAge + "세" : "정보 없음";
                }
                return "정보 없음";
            })
            .collect(Collectors.toList());
    }

    private List<String> extractPaymentPeriods(List<PensionProductDTO> products) {
        return products.stream()
            .map(product -> {
                if (product.getOptions() != null && !product.getOptions().isEmpty()) {
                    List<Integer> periods = product.getOptions().stream()
                        .map(PensionOptionDTO::getPaymPrd)
                        .filter(Objects::nonNull)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());

                    if (!periods.isEmpty()) {
                        return periods.stream()
                            .map(p -> p + "년")
                            .collect(Collectors.joining(", "));
                    }
                }
                return "정보 없음";
            })
            .collect(Collectors.toList());
    }

    private List<String> extractMonthlyPayments(List<PensionProductDTO> products) {
        return products.stream()
            .map(product -> {
                if (product.getOptions() != null && !product.getOptions().isEmpty()) {
                    Integer monPayment = product.getOptions().stream()
                        .map(PensionOptionDTO::getMonPaymAtm)
                        .filter(Objects::nonNull)
                        .min(Integer::compare)
                        .orElse(null);

                    return (monPayment != null)
                        ? String.format("%,d만원", monPayment)
                        : "정보 없음";
                }
                return "정보 없음";
            })
            .collect(Collectors.toList());
    }
}
