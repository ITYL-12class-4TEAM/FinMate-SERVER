package org.scoula.mypage.service;
import lombok.RequiredArgsConstructor;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.mypage.dto.*;
import org.scoula.mypage.mapper.PortfolioMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// PortfolioService.java
@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioMapper portfolioMapper;
    private final MemberMapper memberMapper;
    private final ComparisonService comparisonService;

    public List<PortfolioItemDTO> getPortfolioList() {
        Long memberId = getCurrentUserIdAsLong();
        List<PortfolioItemDTO> items = portfolioMapper.getPortfolioItems(memberId);

        for (PortfolioItemDTO item : items) {
            // 🔍 필요한 데이터 가정: amount, joinDate, saveTerm, customRate
            if (item.getSaveTerm() != null && item.getCustomRate() != null) {
                double rate = item.getCustomRate() / 100.0;
                double periodInYear = item.getSaveTerm() / 12.0;
                // 예상 세전 이자 계산
                double interest = item.getAmount() * rate * periodInYear;

                // 세금 15.4% 계산
                double tax = interest * 0.154;
                double netProfit = interest - tax;

                item.setEstimatedInterest((long) interest);
                item.setEstimatedAfterTax(item.getAmount() + (long) netProfit);

                // 만기일 계산
                LocalDate join = LocalDate.parse(item.getJoinDate());
                LocalDate maturity = join.plusMonths(item.getSaveTerm());
                item.setMaturityDate(maturity.toString());
            }
        }

        return items;
    }

    public void addPortfolio(PortfolioCreateDTO dto) {
        Long memberId = getCurrentUserIdAsLong();

        Long categoryId = portfolioMapper.findCategoryIdByProductId(dto.getProductId());
        Long subcategoryId = portfolioMapper.findSubcategoryIdByProductId(dto.getProductId());
        Long optionId = portfolioMapper.findOptionIdByProductIdAndTerm(dto.getProductId(), dto.getSaveTrm());

        dto.setCategoryId(categoryId);
        dto.setSubcategoryId(subcategoryId);

        portfolioMapper.insertPortfolioItem(memberId, dto);
    }

    public void updatePortfolio(Long portfolioId, PortfolioUpdateDTO dto) {
        portfolioMapper.updatePortfolioItem(portfolioId, dto);
    }

    public void deletePortfolio(Long portfolioId) {
        portfolioMapper.deletePortfolioItem(portfolioId);
    }

    public PortfolioSummaryResponseDTO getSummaryWithComparison() {
        Long memberId = getCurrentUserIdAsLong();

        List<PortfolioSummaryDTO> mySummary = getMyPortfolioSummary(memberId);
        PortfolioComparisonDTO comparison = comparisonService.getComparisonStats(memberId);

        PortfolioSummaryResponseDTO response = new PortfolioSummaryResponseDTO();
        response.setMySummary(mySummary);
        response.setComparisonSummary(comparison);
        return response;
    }

    // 기존 로직을 별도 메서드로 분리
    private List<PortfolioSummaryDTO> getMyPortfolioSummary(Long memberId) {
        List<Map<String, Object>> rawData = portfolioMapper.getPortfolioSummary(memberId);

        Map<String, PortfolioSummaryDTO> grouped = new LinkedHashMap<>();
        long total = 0;

        for (Map<String, Object> row : rawData) {
            String categoryName = (String) row.get("categoryName");
            String subcategoryName = (String) row.get("subcategoryName");
            Long amount = ((Number) row.get("totalAmount")).longValue();

            total += amount;

            PortfolioSummaryDTO category = grouped.computeIfAbsent(categoryName, k -> {
                PortfolioSummaryDTO dto = new PortfolioSummaryDTO();
                dto.setCategoryName(k);
                dto.setSubcategories(new ArrayList<>());
                dto.setTotalAmount(0L);
                return dto;
            });

            SubcategorySummaryDTO sub = new SubcategorySummaryDTO();
            sub.setSubcategoryName(subcategoryName);
            sub.setTotalAmount(amount);
            category.getSubcategories().add(sub);

            category.setTotalAmount(category.getTotalAmount() + amount);
        }

        // 비율 계산
        for (PortfolioSummaryDTO dto : grouped.values()) {
            dto.setRatio((dto.getTotalAmount() * 100.0) / total);
            for (SubcategorySummaryDTO sub : dto.getSubcategories()) {
                sub.setRatio((sub.getTotalAmount() * 100.0) / dto.getTotalAmount());
            }
        }

        return new ArrayList<>(grouped.values());
    }

    private Long getCurrentUserIdAsLong() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberMapper.getMemberIdByEmail(email);
    }
}
