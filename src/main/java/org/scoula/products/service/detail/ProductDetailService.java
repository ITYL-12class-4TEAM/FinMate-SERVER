package org.scoula.products.service.detail;

import org.scoula.products.dto.response.ProductDetailResponse;

/**
 * 상품 상세 정보 서비스 인터페이스
 */
public interface ProductDetailService {
    ProductDetailResponse getDetail(Long productId);
}
