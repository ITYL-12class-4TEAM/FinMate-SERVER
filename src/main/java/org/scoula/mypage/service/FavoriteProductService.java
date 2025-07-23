package org.scoula.mypage.service;

import lombok.RequiredArgsConstructor;
import org.scoula.mypage.dto.FavoriteProductDto;
import org.scoula.mypage.mapper.FavoriteProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteProductService {

    private final FavoriteProductMapper favoriteProductMapper;

    public void addFavorite(Long memberId, Long productId) {
        favoriteProductMapper.insertFavorite(memberId, productId);
    }

    public void removeFavorite(Long memberId, Long productId) {
        favoriteProductMapper.deleteFavorite(memberId, productId);
    }

    public List<FavoriteProductDto> getFavorites(Long memberId) {
        return favoriteProductMapper.selectFavoritesByMemberId(memberId);
    }

    public boolean isFavorite(Long memberId, Long productId) {
        return favoriteProductMapper.existsByMemberIdAndProductId(memberId, productId);
    }
}

