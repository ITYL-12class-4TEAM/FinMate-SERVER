package org.scoula.products.service.api.impl;

import org.scoula.products.dto.response.deposit.DepositProductDTO;
import org.scoula.products.service.api.DepositApiClient;
import org.springframework.stereotype.Service;

@Service
public class DepositApiClientImpl implements DepositApiClient {

    @Override
    public DepositProductDTO getDepositProductDetail(String productId) {
        // 실제 구현 (외부 API 호출, DB 조회 등)
        // 현재는 임시 구현으로 더미 데이터 반환
        DepositProductDTO product = new DepositProductDTO();
        product.setFinPrdtCd(productId);
        product.setKorCoNm("샘플 은행");
        product.setFinPrdtNm("샘플 예금 상품");
        // 필요한 다른 속성 설정

        return product;
    }

    // 기타 다른 메서드 구현...
}