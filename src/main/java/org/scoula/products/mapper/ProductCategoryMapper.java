package org.scoula.products.mapper;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductCategoryMapper {
    /**
     * 모든 카테고리 조회
     */
    @MapKey("category_id")
    List<Map<String, Object>> findAllCategories();

    /**
     * 카테고리 ID로 하위 카테고리 목록 조회
     */
    @MapKey("subcategory_id")
    List<Map<String, Object>> findSubcategoriesByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 카테고리 코드로 카테고리 정보 조회
     */
    @MapKey("category_id")
    Map<String, Object> findCategoryByCode(@Param("code") String code);
}