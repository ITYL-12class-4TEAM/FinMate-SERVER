package org.scoula.mypage.service;

import lombok.RequiredArgsConstructor;
import org.scoula.mypage.dto.FavoriteProductDto;
import org.scoula.mypage.dto.ViewedProductResponseDTO;
import org.scoula.mypage.mapper.ViewedProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecentViewedService {

    private final ViewedProductMapper viewedProductMapper;

    /**
     * 최근 본 상품 저장
     * @param memberId 회원 ID
     * @param productId 상품 ID
     */
    public void saveRecentView(Long memberId, Long productId, Integer saveTrm, String rsrvType) {
        viewedProductMapper.deleteExistingViewedProduct(memberId, productId, saveTrm, rsrvType);
        viewedProductMapper.insertViewedProduct(memberId, productId, saveTrm, rsrvType);
    }

    /**
     * 최근 본 상품 목록 조회
     * @param memberId 회원 ID
     * @return 최근 본 상품 목록
     */
    public List<ViewedProductResponseDTO> getRecentViews(Long memberId) {
        return viewedProductMapper.selectRecentViewedProducts(memberId);
    }

    /**
     * 특정 상품의 최근 본 기록 삭제
     * @param memberId 회원 ID
     * @param productId 상품 ID
     */
    public void deleteRecentView(Long memberId, Long productId) {
        viewedProductMapper.deleteViewedProduct(memberId, productId);
    }

    /**
     * 모든 최근 본 상품 기록 삭제
     * @param memberId 회원 ID
     */
    public void deleteAllRecentViews(Long memberId) {
        viewedProductMapper.deleteAllViewedProducts(memberId);
    }
}
