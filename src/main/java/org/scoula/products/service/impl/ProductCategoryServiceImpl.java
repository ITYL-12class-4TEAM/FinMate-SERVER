package org.scoula.products.service.impl;

import org.scoula.products.mapper.ProductCategoryMapper;
import org.scoula.products.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryMapper categoryMapper;

    @Autowired
    public ProductCategoryServiceImpl(ProductCategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<Map<String, Object>> getAllCategories() {
        return categoryMapper.findAllCategories();
    }

    @Override
    public List<Map<String, Object>> getSubcategoriesByCategoryId(Long categoryId) {
        return categoryMapper.findSubcategoriesByCategoryId(categoryId);
    }

    @Override
    public Map<String, Object> getCategoryByCode(String code) {
        return categoryMapper.findCategoryByCode(code);
    }

    @Override
    public List<Map<String, Object>> getCategoriesWithSubcategories() {
        // 모든 카테고리 조회
        List<Map<String, Object>> categories = categoryMapper.findAllCategories();

        // 각 카테고리에 하위 카테고리 정보 추가
        for (Map<String, Object> category : categories) {
            Long categoryId = Long.valueOf(category.get("id").toString());
            List<Map<String, Object>> subcategories = categoryMapper.findSubcategoriesByCategoryId(categoryId);
            category.put("subcategories", subcategories);
        }

        return categories;
    }
}