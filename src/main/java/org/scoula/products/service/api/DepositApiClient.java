package org.scoula.products.service.api;

import org.scoula.products.dto.response.deposit.DepositProductDTO;

public interface DepositApiClient {
    // 이 메서드가 없다면 추가해야 합니다
    DepositProductDTO getDepositProductDetail(String productId);

    // 기타 다른 메서드들...
}