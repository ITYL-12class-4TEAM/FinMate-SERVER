package org.scoula.mypage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.mypage.dto.FavoriteProductDto;

import java.util.List;

@Mapper
public interface FavoriteProductMapper {
    void insertFavorite(@Param("memberId") Long memberId, @Param("productId") Long productId);
    void increaseWishlistCount(@Param("productId") Long productId);

    void deleteFavorite(@Param("memberId") Long memberId, @Param("productId") Long productId);
    void decreaseWishlistCount(@Param("productId") Long productId);
    
    List<FavoriteProductDto> selectFavoritesByMemberId(@Param("memberId") Long memberId);
    boolean existsByMemberIdAndProductId(@Param("memberId") Long memberId, @Param("productId") Long productId);
}
