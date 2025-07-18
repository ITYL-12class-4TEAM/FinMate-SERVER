package org.scoula.products.service;

import org.scoula.products.dto.response.ProductDetailResponse;
import org.scoula.products.dto.response.ProductListResponse;
import java.util.List;
import java.util.Map;

/**
 * 상품 검색 서비스 인터페이스
 */
public interface ProductSearchService {
    /**
     * 상품 목록 조회
     *
     * @param keyword 검색 키워드 (선택)
     * @param filters 필터 조건 (선택)
     * @param pageNo 페이지 번호
     * @return 상품 목록 응답
     */
    ProductListResponse searchProducts(String keyword, Map<String, String> filters, int pageNo);

    /**
     * 상품 자동완성 검색
     *
     * @param keyword 검색 키워드
     * @return 자동완성 결과 목록
     */
    List<String> autocompleteProducts(String keyword);

    /**
     * 상품 카테고리 목록 조회
     *
     * @return 카테고리 목록
     */
    List<Map<String, Object>> getCategories();

    /**
     * 필터 옵션 조회
     *
     * @return 필터 옵션 목록
     */
    Map<String, Object> getFilterOptions();

    /**
     * 상품 상세 정보 조회
     *
     * @param productId 상품 ID
     * @return 상품 상세 정보
     */
    ProductDetailResponse getProductDetail(String productId);
}