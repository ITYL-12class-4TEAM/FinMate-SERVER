package org.scoula.products.util;

import org.scoula.products.dto.response.FinlifeApiResponse;
import org.scoula.products.dto.response.deposit.DepositOptionDTO;
import org.scoula.products.dto.response.deposit.DepositProductDTO;
import org.scoula.products.dto.response.pension.PensionOptionDTO;
import org.scoula.products.dto.response.pension.PensionProductDTO;
import org.scoula.products.dto.response.saving.SavingOptionDTO;
import org.scoula.products.dto.response.saving.SavingProductDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 금융상품 데이터 변환 유틸리티
 * API 응답 데이터를 내부 DTO로 변환
 */
@Component
public class ProductDataConverter {

    /**
     * 정기예금 상품 목록 변환
     * API 응답을 내부 DTO 목록으로 변환하고 옵션 정보를 매핑
     */
    public List<DepositProductDTO> convertToDepositProducts(FinlifeApiResponse<Map<String, Object>> response) {
        if (response == null || response.getBaseList() == null) {
            return new ArrayList<>();
        }

        // 1. 기본 상품 정보 변환
        List<DepositProductDTO> products = response.getBaseList().stream()
                .map(this::convertToDepositProduct)
                .collect(Collectors.toList());

        // 2. 옵션 정보 변환 및 매핑
        if (response.getOptionList() != null) {
            Map<String, List<DepositOptionDTO>> optionsByProductCode = response.getOptionList().stream()
                    .map(this::convertToDepositOption)
                    .collect(Collectors.groupingBy(DepositOptionDTO::getFinPrdtCd));

            // 3. 각 상품에 해당하는 옵션 목록 설정
            products.forEach(product ->
                product.setOptions(optionsByProductCode.getOrDefault(product.getFinPrdtCd(), new ArrayList<>()))
            );
        }

        return products;
    }

    /**
     * 적금 상품 목록 변환
     * API 응답을 내부 DTO 목록으로 변환하고 옵션 정보를 매핑
     */
    public List<SavingProductDTO> convertToSavingProducts(FinlifeApiResponse<Map<String, Object>> response) {
        if (response == null || response.getBaseList() == null) {
            return new ArrayList<>();
        }

        // 1. 기본 상품 정보 변환
        List<SavingProductDTO> products = response.getBaseList().stream()
                .map(this::convertToSavingProduct)
                .collect(Collectors.toList());

        // 2. 옵션 정보 변환 및 매핑
        if (response.getOptionList() != null) {
            Map<String, List<SavingOptionDTO>> optionsByProductCode = response.getOptionList().stream()
                    .map(this::convertToSavingOption)
                    .collect(Collectors.groupingBy(SavingOptionDTO::getFinPrdtCd));

            // 3. 각 상품에 해당하는 옵션 목록 설정
            products.forEach(product ->
                product.setOptions(optionsByProductCode.getOrDefault(product.getFinPrdtCd(), new ArrayList<>()))
            );
        }

        return products;
    }

    /**
     * 연금저축 상품 목록 변환
     * API 응답을 내부 DTO 목록으로 변환하고 옵션 정보를 매핑
     */
    public List<PensionProductDTO> convertToPensionProducts(FinlifeApiResponse<Map<String, Object>> response) {
        if (response == null || response.getBaseList() == null) {
            return new ArrayList<>();
        }

        // 1. 기본 상품 정보 변환
        List<PensionProductDTO> products = response.getBaseList().stream()
                .map(this::convertToPensionProduct)
                .collect(Collectors.toList());

        // 2. 옵션 정보 변환 및 매핑
        if (response.getOptionList() != null) {
            Map<String, List<PensionOptionDTO>> optionsByProductCode = response.getOptionList().stream()
                    .map(this::convertToPensionOption)
                    .collect(Collectors.groupingBy(PensionOptionDTO::getFinPrdtCd));

            // 3. 각 상품에 해당하는 옵션 목록 설정
            products.forEach(product ->
                product.setOptions(optionsByProductCode.getOrDefault(product.getFinPrdtCd(), new ArrayList<>()))
            );
        }

        return products;
    }

    /**
     * 단일 정기예금 상품 변환
     */
    private DepositProductDTO convertToDepositProduct(Map<String, Object> map) {
        return DepositProductDTO.builder()
                .finCoNo(getStringValue(map, "fin_co_no"))
                .korCoNm(getStringValue(map, "kor_co_nm"))
                .finPrdtCd(getStringValue(map, "fin_prdt_cd"))
                .finPrdtNm(getStringValue(map, "fin_prdt_nm"))
                .joinWay(getStringValue(map, "join_way"))
                .mtrtInt(getStringValue(map, "mtrt_int"))
                .etcNote(getStringValue(map, "etc_note"))
                .spclCnd(getStringValue(map, "spcl_cnd"))
                .joinMember(getStringValue(map, "join_member"))
                .joinDeny(getStringValue(map, "join_deny"))
                .joinAmt(getLongValue(map, "join_amt"))
                .dclsStrtDay(getStringValue(map, "dcls_strt_day"))
                .dclsEndDay(getStringValue(map, "dcls_end_day"))
                .options(new ArrayList<>())  // 옵션은 나중에 설정
                .build();
    }

    /**
     * 단일 적금 상품 변환
     */
    private SavingProductDTO convertToSavingProduct(Map<String, Object> map) {
        return SavingProductDTO.builder()
                .finCoNo(getStringValue(map, "fin_co_no"))
                .korCoNm(getStringValue(map, "kor_co_nm"))
                .finPrdtCd(getStringValue(map, "fin_prdt_cd"))
                .finPrdtNm(getStringValue(map, "fin_prdt_nm"))
                .joinWay(getStringValue(map, "join_way"))
                .mtrtInt(getStringValue(map, "mtrt_int"))
                .etcNote(getStringValue(map, "etc_note"))
                .spclCnd(getStringValue(map, "spcl_cnd"))
                .joinMember(getStringValue(map, "join_member"))
                .joinDeny(getStringValue(map, "join_deny"))
                .joinAmt(getLongValue(map, "join_amt"))
                .dclsStrtDay(getStringValue(map, "dcls_strt_day"))
                .dclsEndDay(getStringValue(map, "dcls_end_day"))
                .rsrvType(getStringValue(map, "rsrv_type"))
                .rsrvTypeNm(getStringValue(map, "rsrv_type_nm"))
                .options(new ArrayList<>())  // 옵션은 나중에 설정
                .build();
    }

    /**
     * 단일 연금저축 상품 변환
     */
    private PensionProductDTO convertToPensionProduct(Map<String, Object> map) {
        return PensionProductDTO.builder()
                .finCoNo(getStringValue(map, "fin_co_no"))
                .korCoNm(getStringValue(map, "kor_co_nm"))
                .finPrdtCd(getStringValue(map, "fin_prdt_cd"))
                .finPrdtNm(getStringValue(map, "fin_prdt_nm"))
                .pnsnKind(getStringValue(map, "pnsn_kind"))
                .pnsnKindNm(getStringValue(map, "pnsn_kind_nm"))
                .joinWay(getStringValue(map, "join_way"))
                .pnsnRcvMthd(getStringValue(map, "pnsn_rcv_mthd"))
                .mnthPymAtm(getLongValue(map, "mnth_pym_atm"))
                .pnsnStrtAge(getIntegerValue(map, "pnsn_strt_age"))
                .pnsnRecpTrm(getStringValue(map, "pnsn_recp_trm"))
                .etcNote(getStringValue(map, "etc_note"))
                .dclsStrtDay(getStringValue(map, "dcls_strt_day"))
                .dclsEndDay(getStringValue(map, "dcls_end_day"))
                .options(new ArrayList<>())  // 옵션은 나중에 설정
                .build();
    }

    /**
     * 정기예금 옵션 정보 변환
     */
    private DepositOptionDTO convertToDepositOption(Map<String, Object> map) {
        return DepositOptionDTO.builder()
                .finPrdtCd(getStringValue(map, "fin_prdt_cd"))
                .saveTrm(getIntegerValue(map, "save_trm"))
                .intrRateType(getStringValue(map, "intr_rate_type"))
                .intrRateTypeNm(getStringValue(map, "intr_rate_type_nm"))
                .intrRate(getDoubleValue(map, "intr_rate"))
                .intrRate2(getDoubleValue(map, "intr_rate2"))
                .build();
    }

    /**
     * 적금 옵션 정보 변환
     */
    private SavingOptionDTO convertToSavingOption(Map<String, Object> map) {
        return SavingOptionDTO.builder()
                .finPrdtCd(getStringValue(map, "fin_prdt_cd"))
                .saveTrm(getIntegerValue(map, "save_trm"))
                .intrRateType(getStringValue(map, "intr_rate_type"))
                .intrRateTypeNm(getStringValue(map, "intr_rate_type_nm"))
                .intrRate(getDoubleValue(map, "intr_rate"))
                .intrRate2(getDoubleValue(map, "intr_rate2"))
                .intrRate2Cnd(getStringValue(map, "intr_rate2_cnd"))
                .build();
    }

    /**
     * 연금저축 옵션 정보 변환
     */
    private PensionOptionDTO convertToPensionOption(Map<String, Object> map) {
        return PensionOptionDTO.builder()
                .finPrdtCd(getStringValue(map, "fin_prdt_cd"))
                .saveTrm(getIntegerValue(map, "save_trm"))
                .intrRateType(getStringValue(map, "intr_rate_type"))
                .intrRateTypeNm(getStringValue(map, "intr_rate_type_nm"))
                .intrRate(getDoubleValue(map, "intr_rate"))
                .intrRate2(getDoubleValue(map, "intr_rate2"))
                .build();
    }

    // 유틸리티 메서드: Map에서 String 값 추출
    private String getStringValue(Map<String, Object> map, String key) {
        return map.containsKey(key) ? String.valueOf(map.get(key)) : null;
    }

    // 유틸리티 메서드: Map에서 Integer 값 추출
    private Integer getIntegerValue(Map<String, Object> map, String key) {
        if (!map.containsKey(key) || map.get(key) == null) {
            return null;
        }
        try {
            if (map.get(key) instanceof Number) {
                return ((Number) map.get(key)).intValue();
            }
            return Integer.parseInt(String.valueOf(map.get(key)));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // 유틸리티 메서드: Map에서 Long 값 추출
    private Long getLongValue(Map<String, Object> map, String key) {
        if (!map.containsKey(key) || map.get(key) == null) {
            return null;
        }
        try {
            if (map.get(key) instanceof Number) {
                return ((Number) map.get(key)).longValue();
            }
            return Long.parseLong(String.valueOf(map.get(key)));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // 유틸리티 메서드: Map에서 Double 값 추출
    private Double getDoubleValue(Map<String, Object> map, String key) {
        if (!map.containsKey(key) || map.get(key) == null) {
            return null;
        }
        try {
            if (map.get(key) instanceof Number) {
                return ((Number) map.get(key)).doubleValue();
            }
            return Double.parseDouble(String.valueOf(map.get(key)));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}