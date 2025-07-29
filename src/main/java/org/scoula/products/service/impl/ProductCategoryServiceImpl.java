package org.scoula.products.service.impl;

import org.scoula.products.dto.response.CategoryDTO;
import org.scoula.products.dto.response.SubcategoryDTO;
import org.scoula.products.mapper.ProductCategoryMapper;
import org.scoula.products.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        // 모든 카테고리 조회
        List<CategoryDTO> categories = getAllCategories();

        // 각 카테고리에 하위 카테고리 정보 추가
        for (CategoryDTO category : categories) {
            List<SubcategoryDTO> subcategories = getSubcategoriesByCategoryId(category.getId());
            category.setSubcategories(subcategories);
        }

        return categories;
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