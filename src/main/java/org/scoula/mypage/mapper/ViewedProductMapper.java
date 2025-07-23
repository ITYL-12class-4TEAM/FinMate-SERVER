package org.scoula.mypage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.mypage.dto.FavoriteProductDto;

import java.util.List;

@Mapper
public interface ViewedProductMapper {

    void insertViewedProduct(@Param("memberId") Long memberId, @Param("productId") Long productId);
    void deleteExistingViewedProduct(@Param("memberId") Long memberId, @Param("productId")Long productId);

    List<FavoriteProductDto> selectRecentViewedProducts(@Param("memberId") Long memberId);

    void deleteViewedProduct(@Param("memberId") Long memberId, @Param("productId") Long productId);

    void deleteAllViewedProducts(@Param("memberId") Long memberId);

}
