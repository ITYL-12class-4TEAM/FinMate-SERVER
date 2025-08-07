package org.scoula.products.service.impl;

import org.scoula.products.dto.response.deposit.DepositProductDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DepositComparisonDataBuilder {

    public enum ComparisonDataKey {
        BASIC_INFO("basicInfo"),
        INTEREST_INFO("interestInfo"),
        TERM_INFO("termInfo"),
        CONDITION_INFO("conditionInfo"),
        ETC_INFO("etcInfo");

        private final String key;

        ComparisonDataKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    public Map<String, Object> build(List<DepositProductDTO> products) {
        Map<String, Object> comparisonData = new HashMap<>();

        comparisonData.put(ComparisonDataKey.BASIC_INFO.getKey(), buildBasicInfo(products));
        comparisonData.put(ComparisonDataKey.INTEREST_INFO.getKey(), buildInterestInfo(products));
        comparisonData.put(ComparisonDataKey.TERM_INFO.getKey(), buildTermInfo(products));
        comparisonData.put(ComparisonDataKey.CONDITION_INFO.getKey(), buildConditionInfo(products));
        comparisonData.put(ComparisonDataKey.ETC_INFO.getKey(), buildEtcInfo(products));

        return comparisonData;
    }

    private Map<String, List<String>> buildBasicInfo(List<DepositProductDTO> products) {
        Map<String, List<String>> basicInfo = new HashMap<>();
        basicInfo.put("은행명", products.stream().map(DepositProductDTO::getKorCoNm).collect(Collectors.toList()));
        basicInfo.put("상품명", products.stream().map(DepositProductDTO::getFinPrdtNm).collect(Collectors.toList()));
        basicInfo.put("상품코드", products.stream().map(DepositProductDTO::getFinPrdtCd).collect(Collectors.toList()));
        return basicInfo;
    }

    private Map<String, List<Double>> buildInterestInfo(List<DepositProductDTO> products) {
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

        Map<String, List<Double>> interestInfo = new HashMap<>();
        interestInfo.put("기본금리(%)", baseRates);
        interestInfo.put("최고금리(%)", maxRates);
        return interestInfo;
    }

    private Map<String, List<Object>> buildTermInfo(List<DepositProductDTO> products) {
        // 각 상품의 저축 기간 추출
        List<Object> terms = products.stream()
                .map(product -> {
                    if (product.getOptions() != null && !product.getOptions().isEmpty()) {
                        return product.getOptions().get(0).getSaveTrm();
                    }
                    return null;
                })
                .collect(Collectors.toList());

        Map<String, List<Object>> termInfo = new HashMap<>();
        termInfo.put("계약기간(개월)", terms);
        return termInfo;
    }

    private Map<String, List<String>> buildConditionInfo(List<DepositProductDTO> products) {
        Map<String, List<String>> conditionInfo = new HashMap<>();

        // 가입방법 - 직접 복사
        List<String> joinWays = new ArrayList<>();
        for (DepositProductDTO product : products) {
            joinWays.add(product.getJoinWay()); // null이어도 그대로 추가
        }
        conditionInfo.put("가입방법", joinWays);

        // 가입대상 - 직접 복사
        List<String> joinMembers = new ArrayList<>();
        for (DepositProductDTO product : products) {
            joinMembers.add(product.getJoinMember()); // null이어도 그대로 추가
        }
        conditionInfo.put("가입대상", joinMembers);

        // 우대조건 - 직접 복사
        List<String> preferentialConditions = new ArrayList<>();
        for (DepositProductDTO product : products) {
            preferentialConditions.add(product.getSpclCnd()); // null이어도 그대로 추가
        }
        conditionInfo.put("우대조건", preferentialConditions);

        return conditionInfo;
    }

    private Map<String, List<String>> buildEtcInfo(List<DepositProductDTO> products) {
        Map<String, List<String>> etcInfo = new HashMap<>();

        // 만기 후 이자율 - 직접 복사
        List<String> mtrtIntRates = new ArrayList<>();
        for (DepositProductDTO product : products) {
            mtrtIntRates.add(product.getMtrtInt()); // null이어도 그대로 추가
        }
        etcInfo.put("만기 후 이자율", mtrtIntRates);

        return etcInfo;
    }
}
