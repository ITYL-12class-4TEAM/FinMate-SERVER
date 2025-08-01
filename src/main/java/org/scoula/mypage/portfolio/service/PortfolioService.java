package org.scoula.mypage.portfolio.service;

import org.scoula.mypage.portfolio.dto.*;

import java.util.List;

public interface PortfolioService {
    // 포트폴리오 목록 조회
    List<PortfolioItemResponse> getPortfolioList();

    // 포트폴리오 추가
    void addPortfolio(PortfolioCreateRequest dto);

    // 포트폴리오 수정
    void updatePortfolio(Long portfolioId, PortfolioUpdateRequest dto);

    // 포트폴리오 삭제
    void deletePortfolio(Long portfolioId);

    // 포트폴리오 요약 정보 조회
    PortfolioSummaryWithComparisonResponse getSummaryWithComparison();
}
