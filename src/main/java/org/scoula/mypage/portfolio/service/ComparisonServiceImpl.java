package org.scoula.mypage.portfolio.service;

import lombok.RequiredArgsConstructor;
import org.scoula.mypage.portfolio.dto.AgeGroupStatResponse;
import org.scoula.mypage.portfolio.dto.CategoryRatioResponse;
import org.scoula.mypage.portfolio.dto.PortfolioComparisonResponse;
import org.scoula.mypage.portfolio.exception.MemberNotFoundException;
import org.scoula.mypage.portfolio.mapper.PortfolioMapper;
import org.scoula.response.ResponseCode;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComparisonServiceImpl implements ComparisonService {
    private final PortfolioMapper portfolioMapper;

    /**
     * 포트폴리오 비교 통계 조회
     * @param memberId 회원 ID
     * @return 비교 통계 데이터
     */
    public PortfolioComparisonResponse getComparisonStats(Long memberId) {
        // 입력값 검증
        if (memberId == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }

        try {
            PortfolioComparisonResponse comparison = new PortfolioComparisonResponse();

            // 각 비교 통계 데이터 조회
            comparison.setByAgeGroup(getAgeGroupComparison(memberId));
            comparison.setByAmountGroup(getAmountGroupComparison(memberId));
            comparison.setByWMTI(getWMTIComparison(memberId));
            comparison.setAgeGroupStats(getPortfolioUserCountByAgeGroup());

            return comparison;
        } catch (Exception e) {
            throw new RuntimeException("포트폴리오 비교 통계 조회 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 같은 연령대 사용자들의 카테고리별 평균 비율 조회
     * @param memberId 회원 ID
     * @return 연령대별 비교 데이터
     */
    private List<CategoryRatioResponse> getAgeGroupComparison(Long memberId) {
        try {
            Integer age = portfolioMapper.getAgeByMemberId(memberId);

            // 나이 정보가 없는 경우 빈 리스트 반환 (정상적인 케이스)
            if (age == null) {
                return Collections.emptyList();
            }

            // 나이 유효성 검증
            if (age < 0 || age > 150) {
                throw new IllegalArgumentException("유효하지 않은 나이 정보입니다: " + age);
            }

            int ageMin = (age / 10) * 10;
            int ageMax = ageMin + 9;

            List<Map<String, Object>> rawData = portfolioMapper.getAgeGroupComparison(memberId, ageMin, ageMax);
            return convertToRatioDTO(rawData);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("연령대별 비교 데이터 조회 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 같은 자산 규모 사용자들의 카테고리별 평균 비율 조회
     * @param memberId 회원 ID
     * @return 자산 규모별 비교 데이터
     */
    private List<CategoryRatioResponse> getAmountGroupComparison(Long memberId) {
        try {
            Long totalAssets = portfolioMapper.getTotalAssetsByMemberId(memberId);

            // 자산 정보가 없거나 0 이하인 경우 빈 리스트 반환 (정상적인 케이스)
            if (totalAssets == null || totalAssets <= 0) {
                return Collections.emptyList();
            }

            // 자산 규모 유효성 검증
            if (totalAssets < 0) {
                throw new IllegalArgumentException("유효하지 않은 자산 규모입니다: " + totalAssets);
            }

            long amountMin = (totalAssets / 10000000) * 10000000;
            long amountMax = amountMin + 9999999;

            List<Map<String, Object>> rawData = portfolioMapper.getAmountGroupComparison(memberId, amountMin, amountMax);
            return convertToRatioDTO(rawData);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("자산 규모별 비교 데이터 조회 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 같은 WMTI 사용자들의 카테고리별 평균 비율 조회
     * @param memberId 회원 ID
     * @return WMTI별 비교 데이터
     */
    private List<CategoryRatioResponse> getWMTIComparison(Long memberId) {
        try {
            String wmtiCode = portfolioMapper.getWmtiCodeByMemberId(memberId);

            // WMTI 정보가 없는 경우 빈 리스트 반환 (정상적인 케이스)
            if (wmtiCode == null || wmtiCode.trim().isEmpty()) {
                return Collections.emptyList();
            }

            // WMTI 코드 유효성 검증 (필요시)
            if (wmtiCode.length() != 4) { // WMTI 코드가 4자리라고 가정
                throw new IllegalArgumentException("유효하지 않은 WMTI 코드입니다: " + wmtiCode);
            }

            List<Map<String, Object>> rawData = portfolioMapper.getWMTIComparison(memberId, wmtiCode);
            return convertToRatioDTO(rawData);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("WMTI별 비교 데이터 조회 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 공통된 DTO 변환 로직
     * @param rawData 원시 데이터
     * @return 변환된 DTO 리스트
     */
    private List<CategoryRatioResponse> convertToRatioDTO(List<Map<String, Object>> rawData) {
        try {
            if (rawData == null) {
                return Collections.emptyList();
            }

            return rawData.stream()
                    .map(row -> {
                        try {
                            CategoryRatioResponse dto = new CategoryRatioResponse();

                            // null 체크 및 타입 안전성 확보
                            String categoryName = (String) row.get("categoryName");
                            Object averageRatioObj = row.get("averageRatio");

                            if (categoryName == null) {
                                throw new IllegalArgumentException("카테고리명이 null입니다.");
                            }

                            double averageRatio = 0.0;
                            if (averageRatioObj instanceof Number) {
                                averageRatio = ((Number) averageRatioObj).doubleValue();
                            }

                            dto.setCategoryName(categoryName);
                            dto.setAverageRatio(averageRatio);
                            return dto;
                        } catch (Exception e) {
                            throw new RuntimeException("DTO 변환 중 오류가 발생했습니다.", e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("데이터 변환 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 포트폴리오 사용자 수를 나이대별로 조회
     * @return 나이대별 사용자 수 통계
     */
    public List<AgeGroupStatResponse> getPortfolioUserCountByAgeGroup() {
        try {
            List<Map<String, Object>> raw = portfolioMapper.getPortfolioMemberCountByAgeGroup();

            if (raw == null) {
                return Collections.emptyList();
            }

            return raw.stream()
                    .map(row -> {
                        try {
                            AgeGroupStatResponse dto = new AgeGroupStatResponse();

                            // null 체크 및 타입 안전성 확보
                            Object ageGroupObj = row.get("age_group");
                            Object userCountObj = row.get("user_count");

                            if (ageGroupObj instanceof Number && userCountObj instanceof Number) {
                                dto.setAgeGroup(String.valueOf(((Number) ageGroupObj).intValue()));
                                dto.setUserCount(((Number) userCountObj).longValue());
                            } else {
                                throw new IllegalArgumentException("통계 데이터 형식이 올바르지 않습니다.");
                            }

                            return dto;
                        } catch (Exception e) {
                            throw new RuntimeException("나이대별 통계 DTO 변환 중 오류가 발생했습니다.", e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("나이대별 사용자 수 조회 중 오류가 발생했습니다.", e);
        }
    }
}