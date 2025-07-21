package org.scoula.products.service;

import org.scoula.products.dto.request.ProductSearchRequest;
import org.scoula.products.dto.response.ProductDetailResponse;
import org.scoula.products.dto.response.ProductListResponse;

import java.util.List;
import java.util.Map;

/**
 * 금융 상품 검색 서비스 인터페이스
 */
public interface ProductSearchService {

    /**
     * 상품 검색 요청에 따라 상품 목록을 조회합니다.
     *
     * @param request 검색 요청 정보
     * @return 검색 결과 목록
     */
    ProductListResponse searchProducts(ProductSearchRequest request);

    /**
     * 키워드와 필터를 기반으로 상품 목록을 조회합니다.
     *
     * @param keyword 검색 키워드
     * @param filters 필터 맵 (카테고리, 금리 유형 등)
     * @param pageNo 페이지 번호
     * @return 검색 결과 목록
     */
    ProductListResponse searchProducts(String keyword, Map<String, String> filters, int pageNo);


    /**
     * 상품 ID로 상세 정보를 조회합니다.
     *
     * @param productType 상품 유형 (deposit, saving, pension)
     * @param productId 상품 ID
     * @return 상품 상세 정보
     */
    ProductDetailResponse getProductDetail(String productType, String productId);

    /**
     * 검색어 자동완성 제안을 제공합니다.
     *
     * @param keyword 검색 키워드
     * @return 자동완성 제안 목록
     */
    List<String> autocompleteProducts(String keyword);
}