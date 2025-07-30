package org.scoula.mypage.favorite.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.mypage.favorite.dto.SubcategoryResponse;

import java.util.List;

@Mapper
public interface ProductMapper {
    // 카테고리별 서브카테고리 목록 조회
    List<SubcategoryResponse> getAllSubcategories();
}
