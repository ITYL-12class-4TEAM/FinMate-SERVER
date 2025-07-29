package org.scoula.mypage.service;

import lombok.RequiredArgsConstructor;
import org.scoula.mypage.dto.CategoryRatioDTO;
import org.scoula.mypage.dto.PortfolioComparisonDTO;
import org.scoula.mypage.mapper.PortfolioMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor  // 의존성 주입
public class ComparisonService {

    private final PortfolioMapper portfolioMapper;  // 추가

    public PortfolioComparisonDTO getComparisonStats(Long memberId) {
        PortfolioComparisonDTO comparison = new PortfolioComparisonDTO();

        comparison.setByAgeGroup(getAgeGroupComparison(memberId));
        comparison.setByAmountGroup(getAmountGroupComparison(memberId));
        comparison.setByWMTI(getWMTIComparison(memberId));

        return comparison;
    }

    private List<CategoryRatioDTO> getAgeGroupComparison(Long memberId) {
        // 나이 계산 로직 추가
        Integer age = portfolioMapper.getAgeByMemberId(memberId);
        if (age == null) return List.of();

        int ageMin = (age / 10) * 10;      // 20대면 20
        int ageMax = ageMin + 9;           // 20대면 29

        List<Map<String, Object>> rawData = portfolioMapper.getAgeGroupComparison(memberId, ageMin, ageMax);
        return convertToRatioDTO(rawData);
    }

    private List<CategoryRatioDTO> getAmountGroupComparison(Long memberId) {
        // 자산 규모 계산 로직 추가
        Long totalAssets = portfolioMapper.getTotalAssetsByMemberId(memberId);
        if (totalAssets == null || totalAssets <= 0) return List.of();

        long amountMin = (totalAssets / 10000000) * 10000000;  // 1천만원 단위
        long amountMax = amountMin + 9999999;

        List<Map<String, Object>> rawData = portfolioMapper.getAmountGroupComparison(memberId, amountMin, amountMax);
        return convertToRatioDTO(rawData);
    }

    private List<CategoryRatioDTO> getWMTIComparison(Long memberId) {
        // WMTI 코드 조회 로직 추가
        String wmtiCode = portfolioMapper.getWmtiCodeByMemberId(memberId);
        if (wmtiCode == null) return List.of();

        List<Map<String, Object>> rawData = portfolioMapper.getWMTIComparison(memberId, wmtiCode);
        return convertToRatioDTO(rawData);
    }

    // Map을 DTO로 변환하는 헬퍼 메서드 추가
    private List<CategoryRatioDTO> convertToRatioDTO(List<Map<String, Object>> rawData) {
        return rawData.stream()
                .map(row -> {
                    CategoryRatioDTO dto = new CategoryRatioDTO();
                    dto.setCategoryName((String) row.get("categoryName"));
                    dto.setAverageRatio(((Number) row.get("averageRatio")).doubleValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}