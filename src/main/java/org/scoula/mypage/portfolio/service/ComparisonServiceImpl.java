package org.scoula.mypage.portfolio.service;

import lombok.RequiredArgsConstructor;
import org.scoula.mypage.portfolio.dto.AgeGroupStatResponse;
import org.scoula.mypage.portfolio.dto.CategoryRatioResponse;
import org.scoula.mypage.portfolio.dto.PortfolioComparisonResponse;
import org.scoula.mypage.portfolio.mapper.PortfolioMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComparisonServiceImpl implements  ComparisonService{
    private final PortfolioMapper portfolioMapper;

    // 포트폴리오 비교 통계 조회
    public PortfolioComparisonResponse getComparisonStats(Long memberId) {
        PortfolioComparisonResponse comparison = new PortfolioComparisonResponse();

        comparison.setByAgeGroup(getAgeGroupComparison(memberId));
        comparison.setByAmountGroup(getAmountGroupComparison(memberId));
        comparison.setByWMTI(getWMTIComparison(memberId));

        comparison.setAgeGroupStats(getPortfolioUserCountByAgeGroup());

        return comparison;
    }

    // 현재 로그인한 사용자의 ID를 Long 타입으로 가져오는 메서드
    private List<CategoryRatioResponse> getAgeGroupComparison(Long memberId) {
        Integer age = portfolioMapper.getAgeByMemberId(memberId);
        if (age == null) return List.of();

        int ageMin = (age / 10) * 10;
        int ageMax = ageMin + 9;

        List<Map<String, Object>> rawData = portfolioMapper.getAgeGroupComparison(memberId, ageMin, ageMax);
        return convertToRatioDTO(rawData);
    }

    // 같은 자산 규모 사용자들의 카테고리별 평균 비율 조회
    private List<CategoryRatioResponse> getAmountGroupComparison(Long memberId) {
        Long totalAssets = portfolioMapper.getTotalAssetsByMemberId(memberId);
        if (totalAssets == null || totalAssets <= 0) return List.of();

        long amountMin = (totalAssets / 10000000) * 10000000;
        long amountMax = amountMin + 9999999;

        List<Map<String, Object>> rawData = portfolioMapper.getAmountGroupComparison(memberId, amountMin, amountMax);
        return convertToRatioDTO(rawData);
    }

    // 같은 WMTI 사용자들의 카테고리별 평균 비율 조회
    private List<CategoryRatioResponse> getWMTIComparison(Long memberId) {
        String wmtiCode = portfolioMapper.getWmtiCodeByMemberId(memberId);
        if (wmtiCode == null) return List.of();

        List<Map<String, Object>> rawData = portfolioMapper.getWMTIComparison(memberId, wmtiCode);
        return convertToRatioDTO(rawData);
    }

    // 공통된 DTO 변환 로직
    private List<CategoryRatioResponse> convertToRatioDTO(List<Map<String, Object>> rawData) {
        return rawData.stream()
                .map(row -> {
                    CategoryRatioResponse dto = new CategoryRatioResponse();
                    dto.setCategoryName((String) row.get("categoryName"));
                    dto.setAverageRatio(((Number) row.get("averageRatio")).doubleValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 포트폴리오 사용자 수를 나이대별로 조회
    public List<AgeGroupStatResponse> getPortfolioUserCountByAgeGroup() {
        List<Map<String, Object>> raw = portfolioMapper.getPortfolioMemberCountByAgeGroup();
        return raw.stream().map(row -> {
            AgeGroupStatResponse dto = new AgeGroupStatResponse();
            dto.setAgeGroup(String.valueOf(((Number) row.get("age_group")).intValue()));
            dto.setUserCount(((Number) row.get("user_count")).longValue());
            return dto;
        }).collect(Collectors.toList());
    }
}
