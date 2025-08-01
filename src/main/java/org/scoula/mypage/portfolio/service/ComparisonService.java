package org.scoula.mypage.portfolio.service;

import org.scoula.mypage.portfolio.dto.AgeGroupStatResponse;
import org.scoula.mypage.portfolio.dto.PortfolioComparisonResponse;

import java.util.List;

public interface ComparisonService {
    // 포트폴리오 비교 통계 조회
    PortfolioComparisonResponse getComparisonStats(Long memberId);

    // 연령대별 사용자 수 조회
    List<AgeGroupStatResponse> getPortfolioUserCountByAgeGroup();
}