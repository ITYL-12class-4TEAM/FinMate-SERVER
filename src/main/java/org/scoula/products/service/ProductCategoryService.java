package org.scoula.products.service;

import java.util.List;
import java.util.Map;

public interface ProductCategoryService {
    /**
     * 모든 카테고리 목록 조회
     * @return 카테고리 목록
     */
    List<Map<String, Object>> getAllCategories();

    /**
     * 카테고리별 하위 카테고리 조회
     * @param categoryId 카테고리 ID
     * @return 하위 카테고리 목록
     */
    List<Map<String, Object>> getSubcategoriesByCategoryId(Long categoryId);

    /**
     * 카테고리 코드로 카테고리 정보 조회
     * @param code 카테고리 코드
     * @return 카테고리 정보
     */
    Map<String, Object> getCategoryByCode(String code);

    /**
     * 카테고리 및 하위 카테고리 정보를 포함한 카테고리 목록 조회
     * @return 카테고리 및 하위 카테고리 정보
     */
    List<Map<String, Object>> getCategoriesWithSubcategories();
}