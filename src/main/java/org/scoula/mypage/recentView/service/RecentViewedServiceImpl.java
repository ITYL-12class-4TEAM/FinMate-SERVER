package org.scoula.mypage.recentView.service;

import lombok.RequiredArgsConstructor;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.mypage.recentView.dto.RecentProductResponse;
import org.scoula.mypage.recentView.mapper.ViewedProductMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecentViewedServiceImpl implements RecentViewedService {
    private final ViewedProductMapper viewedProductMapper;
    private final MemberMapper memberMapper;

    /**
     * 최근 본 상품 저장
     * @param productId 상품 ID
     */
    public void saveRecentView(Long productId, Integer saveTrm, String rsrvType) {
        Long memberId = getCurrentUserIdAsLong();

        viewedProductMapper.deleteExistingViewedProduct(memberId, productId, saveTrm, rsrvType);
        viewedProductMapper.insertViewedProduct(memberId, productId, saveTrm, rsrvType);
    }

    /**
     * 최근 본 상품 목록 조회
     * @return 최근 본 상품 목록
     */
    public List<RecentProductResponse> getRecentViews() {
        Long memberId = getCurrentUserIdAsLong();

        return viewedProductMapper.selectRecentViewedProducts(memberId);
    }

    /**
     * 특정 상품의 최근 본 기록 삭제
     * @param productId 상품 ID
     */
    public void deleteRecentView(Long productId) {
        Long memberId = getCurrentUserIdAsLong();

        viewedProductMapper.deleteViewedProduct(memberId, productId);
    }

    /**
     * 모든 최근 본 상품 기록 삭제
     */
    public void deleteAllRecentViews()
    {
        Long memberId = getCurrentUserIdAsLong();
        viewedProductMapper.deleteAllViewedProducts(memberId);
    }
    private Long getCurrentUserIdAsLong() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberMapper.getMemberIdByEmail(email);
    }
}
