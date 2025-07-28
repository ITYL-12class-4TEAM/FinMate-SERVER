package org.scoula.mypage.service;
import lombok.RequiredArgsConstructor;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.mypage.dto.*;
import org.scoula.mypage.mapper.PortfolioMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

    public List<PortfolioItemDTO> getPortfolioList() {
        Long memberId = getCurrentUserIdAsLong();

        return portfolioMapper.getPortfolioItems(memberId);
    }

    public void addPortfolio(PortfolioCreateDTO dto) {
        Long memberId = getCurrentUserIdAsLong();

        Long categoryId = portfolioMapper.findCategoryIdByProductId(dto.getProductId());
        Long subcategoryId = portfolioMapper.findSubcategoryIdByProductId(dto.getProductId());

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

    public List<PortfolioSummaryDTO> getSummary() {
        Long memberId = getCurrentUserIdAsLong();

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
