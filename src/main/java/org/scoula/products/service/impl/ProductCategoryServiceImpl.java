package org.scoula.products.service.impl;

import org.scoula.products.dto.response.CategoryDTO;
import org.scoula.products.dto.response.SubcategoryDTO;
import org.scoula.products.mapper.ProductCategoryMapper;
import org.scoula.products.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryMapper categoryMapper;

    @Autowired
    public ProductCategoryServiceImpl(ProductCategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryMapper.findAllCategories().stream()
                .map(this::mapToCategory)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubcategoryDTO> getSubcategoriesByCategoryId(Long categoryId) {
        return categoryMapper.findSubcategoriesByCategoryId(categoryId).stream()
                .map(this::mapToSubcategory)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO getCategoryByCode(String code) {
        return mapToCategory(categoryMapper.findCategoryByCode(code));
    }

    @Override
    public List<CategoryDTO> getCategoriesWithSubcategories() {
        // 1. 모든 카테고리 먼저 조회
        List<CategoryDTO> categories = getAllCategories();

        if (categories.isEmpty()) {
            return categories;
        }

        // 2. 모든 카테고리 ID 목록 추출
        List<Long> categoryIds = categories.stream()
                .map(CategoryDTO::getId)
                .collect(Collectors.toList());

        // 3. 모든 하위 카테고리를 한 번에 조회
        List<SubcategoryDTO> allSubcategories = getAllSubcategoriesByMultipleCategoryIds(categoryIds);

        // 4. 카테고리 ID를 기준으로 하위 카테고리 그룹화
        Map<Long, List<SubcategoryDTO>> subcategoriesByCategory = allSubcategories.stream()
                .collect(Collectors.groupingBy(SubcategoryDTO::getCategoryId));

        // 5. 각 카테고리에 해당하는 하위 카테고리 할당
        for (CategoryDTO category : categories) {
            List<SubcategoryDTO> subcategories = subcategoriesByCategory.getOrDefault(category.getId(), Collections.emptyList());
            category.setSubcategories(subcategories);
        }

        return categories;
    }

    /**
     * 여러 카테고리 ID에 대한 모든 하위 카테고리 조회
     */
    private List<SubcategoryDTO> getAllSubcategoriesByMultipleCategoryIds(List<Long> categoryIds) {
        return categoryMapper.findSubcategoriesByMultipleCategoryIds(categoryIds).stream()
                .map(this::mapToSubcategory)
                .collect(Collectors.toList());
    }


    /**
     * Map을 CategoryDTO로 변환
     */
    private CategoryDTO mapToCategory(Map<String, Object> map) {
        if (map == null) return null;

        return CategoryDTO.builder()
                .id(Long.valueOf(map.get("id").toString()))
                .name((String) map.get("name"))
                .description((String) map.get("description"))
                .build();
    }

    /**
     * Map을 SubcategoryDTO로 변환
     */
    private SubcategoryDTO mapToSubcategory(Map<String, Object> map) {
        if (map == null) return null;

        return SubcategoryDTO.builder()
                .id(Long.valueOf(map.get("id").toString()))
                .categoryId(Long.valueOf(map.get("categoryId").toString()))
                .name((String) map.get("name"))
                .description((String) map.get("description"))
                .build();
    }
}