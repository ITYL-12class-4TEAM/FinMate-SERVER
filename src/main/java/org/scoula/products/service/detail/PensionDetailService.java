package org.scoula.products.service.detail;

import org.scoula.products.dto.response.ProductDetailResponse;
import org.scoula.products.dto.response.pension.PensionOptionDTO;
import org.scoula.products.dto.response.pension.PensionProductDTO;
import org.scoula.products.exception.ProductNotFoundException;
import org.scoula.products.mapper.PensionProductMapper;
import org.scoula.response.ResponseCode;

import java.util.List;

/**
 * 연금 상품 상세 정보 서비스
 */
public class PensionDetailService implements ProductDetailService {
    private final PensionProductMapper pensionProductMapper;

    public PensionDetailService(PensionProductMapper pensionProductMapper) {
        this.pensionProductMapper = pensionProductMapper;
    }

    @Override
    public ProductDetailResponse getDetail(Long productId) {
        PensionProductDTO pension = pensionProductMapper.findByProductId(productId);

        if (pension == null) {
            throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
        }

        // 옵션 정보 조회
        List<PensionOptionDTO> options = pensionProductMapper.findOptionsByProductId(productId);
        pension.setOptions(options);

        return new ProductDetailResponse(pension, "pension");
    }
}
