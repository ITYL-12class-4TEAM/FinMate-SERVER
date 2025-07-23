package org.scoula.mypage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.mypage.dto.SubcategoryDto;

import java.util.List;

@Mapper
public interface ProductMapper {
    List<SubcategoryDto> getAllSubcategories();
}
