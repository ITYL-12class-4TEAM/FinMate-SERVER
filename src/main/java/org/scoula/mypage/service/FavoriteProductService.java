package org.scoula.mypage.service;

import lombok.RequiredArgsConstructor;
import org.scoula.mypage.dto.FavoriteProductDto;
import org.scoula.mypage.dto.PopularProductGroupDto;
import org.scoula.mypage.dto.SubcategoryDto;
import org.scoula.mypage.mapper.FavoriteProductMapper;
import org.scoula.mypage.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteProductService {

    private final FavoriteProductMapper favoriteProductMapper;
    private final ProductMapper productMapper;


    public void addFavorite(Long memberId, Long productId) {
        favoriteProductMapper.insertFavorite(memberId, productId);
        favoriteProductMapper.increaseWishlistCount(productId);

    }

    public void removeFavorite(Long memberId, Long productId) {
        favoriteProductMapper.deleteFavorite(memberId, productId);
        favoriteProductMapper.decreaseWishlistCount(productId);
    }

    public List<FavoriteProductDto> getFavorites(Long memberId) {
        return favoriteProductMapper.selectFavoritesByMemberId(memberId);
    }

    public boolean isFavorite(Long memberId, Long productId) {
        return favoriteProductMapper.existsByMemberIdAndProductId(memberId, productId);
    }

    public List<PopularProductGroupDto> getPopularFavoritesByCategory(int days) {
        List<SubcategoryDto> subcategories = productMapper.getAllSubcategories();

        return subcategories.stream()
                .map(sub -> {
                    List<FavoriteProductDto> products =
                            favoriteProductMapper.selectPopularFavorites(sub.getSubcategoryId(), days);
                    PopularProductGroupDto dto = new PopularProductGroupDto();
                    dto.setSubcategoryId(sub.getSubcategoryId());
                    dto.setSubcategoryName(sub.getSubcategoryName());
                    dto.setProducts(products);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}

