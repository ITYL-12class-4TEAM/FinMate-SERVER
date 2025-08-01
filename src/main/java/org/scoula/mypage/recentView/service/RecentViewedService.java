package org.scoula.mypage.recentView.service;

import org.scoula.mypage.recentView.dto.RecentProductResponse;

import java.util.List;

public interface RecentViewedService {
    // 최근 본 상품 저장
    void saveRecentView(Long productId, Integer saveTrm, String rsrvType);

    // 최근 본 상품 목록 조회
    List<RecentProductResponse> getRecentViews();

    // 특정 상품이 최근 본 목록에 있는지 확인
    void deleteRecentView(Long productId);

    // 특정 상품이 최근 본 목록에 있는지 확인
    void deleteAllRecentViews();
}
