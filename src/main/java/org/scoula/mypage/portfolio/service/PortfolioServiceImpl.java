package org.scoula.mypage.portfolio.service;

import lombok.RequiredArgsConstructor;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.mypage.portfolio.dto.*;
import org.scoula.mypage.portfolio.mapper.PortfolioMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {
    private final PortfolioMapper portfolioMapper;
    private final MemberMapper memberMapper;
    private final ComparisonService comparisonService;

    // 포트폴리오 목록 조회
    public List<PortfolioItemResponse> getPortfolioList() {
        Long memberId = getCurrentUserIdAsLong();
        List<PortfolioItemResponse> items = portfolioMapper.getPortfolioItems(memberId);

        for (PortfolioItemResponse item : items) {
            if (item.getSaveTerm() != null && item.getCustomRate() != null) {
                double rate = item.getCustomRate() / 100.0;
                double periodInYear = item.getSaveTerm() / 12.0;
                double interest = item.getAmount() * rate * periodInYear;

                // 세금 15.4% 계산
                double tax = interest * 0.154;
                double netProfit = interest - tax;

                item.setEstimatedInterest((long) interest);
                item.setEstimatedAfterTax(item.getAmount() + (long) netProfit);

                LocalDate join = LocalDate.parse(item.getJoinDate());
                LocalDate maturity = join.plusMonths(item.getSaveTerm());
                item.setMaturityDate(maturity.toString());
            }
        }

        return items;
    }

    // 포트폴리오 추가
    public void addPortfolio(PortfolioCreateRequest dto) {
        Long memberId = getCurrentUserIdAsLong();

        Long categoryId = portfolioMapper.findCategoryIdByProductId(dto.getProductId());
        Long subcategoryId = portfolioMapper.findSubcategoryIdByProductId(dto.getProductId());
        Long optionId = portfolioMapper.findOptionIdByProductIdAndTerm(dto.getProductId(), dto.getSaveTrm());

        dto.setCategoryId(categoryId);
        dto.setSubcategoryId(subcategoryId);

        portfolioMapper.insertPortfolioItem(memberId, dto);
    }

    // 포트폴리오 수정
    public void updatePortfolio(Long portfolioId, PortfolioUpdateRequest dto) {
        portfolioMapper.updatePortfolioItem(portfolioId, dto);
    }

    // 포트폴리오 삭제
    public void deletePortfolio(Long portfolioId) {
        portfolioMapper.deletePortfolioItem(portfolioId);
    }

    // 포트폴리오 요약 정보 조회 + 비교 통계
    public PortfolioSummaryWithComparisonResponse getSummaryWithComparison() {
        Long memberId = getCurrentUserIdAsLong();

        List<PortfolioSummaryResponse> mySummary = getMyPortfolioSummary(memberId);
        PortfolioComparisonResponse comparison = comparisonService.getComparisonStats(memberId);

        PortfolioSummaryWithComparisonResponse response = new PortfolioSummaryWithComparisonResponse();
        response.setMySummary(mySummary);
        response.setComparisonSummary(comparison);
        return response;
    }

    // 개인 포트폴리오 요약 정보 조회
    private List<PortfolioSummaryResponse> getMyPortfolioSummary(Long memberId) {
        List<Map<String, Object>> rawData = portfolioMapper.getPortfolioSummary(memberId);

        Map<String, PortfolioSummaryResponse> grouped = new LinkedHashMap<>();
        long total = 0;

        for (Map<String, Object> row : rawData) {
            String categoryName = (String) row.get("categoryName");
            String subcategoryName = (String) row.get("subcategoryName");
            Long amount = ((Number) row.get("totalAmount")).longValue();

            total += amount;

            PortfolioSummaryResponse category = grouped.computeIfAbsent(categoryName, k -> {
                PortfolioSummaryResponse dto = new PortfolioSummaryResponse();
                dto.setCategoryName(k);
                dto.setSubcategories(new ArrayList<>());
                dto.setTotalAmount(0L);
                return dto;
            });

            SubcategorySummaryResponse sub = new SubcategorySummaryResponse();
            sub.setSubcategoryName(subcategoryName);
            sub.setTotalAmount(amount);
            category.getSubcategories().add(sub);

            category.setTotalAmount(category.getTotalAmount() + amount);
        }

        for (PortfolioSummaryResponse dto : grouped.values()) {
            dto.setRatio((dto.getTotalAmount() * 100.0) / total);
            for (SubcategorySummaryResponse sub : dto.getSubcategories()) {
                sub.setRatio((sub.getTotalAmount() * 100.0) / dto.getTotalAmount());
            }
        }

        return new ArrayList<>(grouped.values());
    }

    // 현재 로그인한 사용자의 ID를 Long 타입으로 반환
    private Long getCurrentUserIdAsLong() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberMapper.getMemberIdByEmail(email);
    }
}
