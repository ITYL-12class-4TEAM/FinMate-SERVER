package org.scoula.products.config;

import org.scoula.products.dto.response.deposit.DepositOptionDTO;
import org.scoula.products.dto.response.deposit.DepositProductDTO;
import org.scoula.products.service.api.DepositApiClient;
import org.scoula.products.service.api.PensionApiClient;
import org.scoula.products.service.api.SavingApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ApiClientConfig {

    @Bean
    public DepositApiClient depositApiClient() {
        return new DepositApiClient() {
            @Override
            public DepositProductDTO getDepositProductDetail(String productId) {
                return createSampleDepositProduct(productId);
            }
        };
    }

    @Bean
    public SavingApiClient savingApiClient() {
        return new SavingApiClient() {
            @Override
            public DepositProductDTO getDepositProductDetail(String productId) {
                return createSampleDepositProduct(productId);
            }
        };
    }

    @Bean
    public PensionApiClient pensionApiClient() {
        return new PensionApiClient() {
            @Override
            public DepositProductDTO getDepositProductDetail(String productId) {
                return createSampleDepositProduct(productId);
            }
        };
    }

    // 샘플 데이터 생성 헬퍼 메서드
    private DepositProductDTO createSampleDepositProduct(String productId) {
        DepositProductDTO product = new DepositProductDTO();
        product.setFinPrdtCd(productId);
        product.setKorCoNm("샘플 은행");
        product.setFinPrdtNm("샘플 예금 상품");
        product.setJoinWay("인터넷뱅킹,스마트폰뱅킹,창구");
        product.setMtrtInt("만기 후 자동 해지");
        product.setSpclCnd("우대금리 0.5% 적용");
        product.setJoinMember("제한없음");
        product.setJoinDeny("없음");
        product.setJoinAmt(10000000L);
        product.setDclsStrtDay("20250101");

        // 옵션 추가
        List<DepositOptionDTO> options = new ArrayList<>();
        DepositOptionDTO option = new DepositOptionDTO();
        option.setFinPrdtCd(productId);
        option.setSaveTrm(12);
        option.setIntrRateType("S");
        option.setIntrRateTypeNm("단리");
        option.setIntrRate(3.5);
        option.setIntrRate2(4.0);
        options.add(option);

        product.setOptions(options);
        return product;
    }
}