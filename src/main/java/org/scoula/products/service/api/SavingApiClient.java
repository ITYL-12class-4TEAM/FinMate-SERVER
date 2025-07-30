package org.scoula.products.service.api;

import org.scoula.products.dto.response.deposit.DepositProductDTO;

public interface SavingApiClient {
    DepositProductDTO getDepositProductDetail(String productId);
}