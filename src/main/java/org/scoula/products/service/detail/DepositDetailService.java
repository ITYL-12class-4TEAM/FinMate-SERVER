package org.scoula.products.service.detail;

import org.scoula.products.dto.response.ProductDetailResponse;
import org.scoula.products.dto.response.deposit.DepositOptionDTO;
import org.scoula.products.dto.response.deposit.DepositProductDTO;
import org.scoula.products.exception.ProductNotFoundException;
import org.scoula.products.mapper.DepositProductMapper;
import org.scoula.response.ResponseCode;

import java.util.List;

/**
 * 예금 상품 상세 정보 서비스
 */
public class DepositDetailService implements ProductDetailService {
    private final DepositProductMapper depositProductMapper;

    public DepositDetailService(DepositProductMapper depositProductMapper) {
        this.depositProductMapper = depositProductMapper;
    }

    @Override
    public ProductDetailResponse getDetail(Long productId) {
        DepositProductDTO deposit = depositProductMapper.findByProductId(productId);

        if (deposit == null) {
            throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
        }

        // 옵션 정보 조회
        List<DepositOptionDTO> options = depositProductMapper.findOptionsByProductId(productId);
        deposit.setOptions(options);

        return new ProductDetailResponse(deposit, "deposit");
    }
}
