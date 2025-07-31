package org.scoula.mypage.portfolio.service;

import lombok.RequiredArgsConstructor;
import org.scoula.mypage.favorite.exception.ValidationException;
import org.scoula.mypage.portfolio.dto.AgeGroupStatResponse;
import org.scoula.mypage.portfolio.dto.CategoryRatioResponse;
import org.scoula.mypage.portfolio.dto.PortfolioComparisonResponse;
import org.scoula.mypage.portfolio.exception.ComparisonServiceException;
import org.scoula.mypage.portfolio.exception.DataConversionException;
import org.scoula.mypage.portfolio.exception.StatisticsException;
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
        validateMemberId(memberId);

        try {
            PortfolioComparisonResponse comparison = new PortfolioComparisonResponse();

            // 각 비교 통계 데이터 조회
            comparison.setByAgeGroup(getAgeGroupComparison(memberId));
            comparison.setByAmountGroup(getAmountGroupComparison(memberId));
            comparison.setByWMTI(getWMTIComparison(memberId));
            comparison.setAgeGroupStats(getPortfolioUserCountByAgeGroup());

            return comparison;
        } catch (Exception e) {
            throw new ComparisonServiceException(ResponseCode.COMPARISON_STATS_FAILED);
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
            validateAge(age);

            AgeRange ageRange = calculateAgeRange(age);
            List<Map<String, Object>> rawData = portfolioMapper.getAgeGroupComparison(memberId, ageRange.getMin(), ageRange.getMax());

            return convertToRatioDTO(rawData, "연령대별 비교 데이터");
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new ComparisonServiceException(ResponseCode.AGE_GROUP_COMPARISON_FAILED);
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
            validateAssetAmount(totalAssets);

            AssetRange assetRange = calculateAssetRange(totalAssets);
            List<Map<String, Object>> rawData = portfolioMapper.getAmountGroupComparison(memberId, assetRange.getMin(), assetRange.getMax());

            return convertToRatioDTO(rawData, "자산 규모별 비교 데이터");
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new ComparisonServiceException(ResponseCode.AMOUNT_GROUP_COMPARISON_FAILED);
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

            // WMTI 코드 유효성 검증
            validateWmtiCode(wmtiCode);

            List<Map<String, Object>> rawData = portfolioMapper.getWMTIComparison(memberId, wmtiCode);
            return convertToRatioDTO(rawData, "WMTI별 비교 데이터");
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new ComparisonServiceException(ResponseCode.WMTI_COMPARISON_FAILED);
        }
    }

    /**
     * 공통된 DTO 변환 로직
     * @param rawData 원시 데이터
     * @param dataType 데이터 타입 (로그용)
     * @return 변환된 DTO 리스트
     */
    private List<CategoryRatioResponse> convertToRatioDTO(List<Map<String, Object>> rawData, String dataType) {
        try {
            if (rawData == null) {
                return Collections.emptyList();
            }

            return rawData.stream()
                    .map(row -> convertSingleRowToRatioDTO(row))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new DataConversionException(ResponseCode.DATA_CONVERSION_FAILED);
        }
    }

    /**
     * 단일 행 데이터를 CategoryRatioResponse DTO로 변환
     */
    private CategoryRatioResponse convertSingleRowToRatioDTO(Map<String, Object> row) {
        try {
            CategoryRatioResponse dto = new CategoryRatioResponse();

            // null 체크 및 타입 안전성 확보
            String categoryName = (String) row.get("categoryName");
            Object averageRatioObj = row.get("averageRatio");

            if (categoryName == null) {
                throw new IllegalArgumentException("카테고리명이 null입니다.");
            }

            double averageRatio = extractDoubleValue(averageRatioObj);

            dto.setCategoryName(categoryName);
            dto.setAverageRatio(averageRatio);
            return dto;
        } catch (Exception e) {
            throw new DataConversionException(ResponseCode.DTO_CONVERSION_FAILED);
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
                    .map(this::convertToAgeGroupStatResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new StatisticsException(ResponseCode.AGE_GROUP_STATS_FAILED);
        }
    }

    /**
     * 단일 행 데이터를 AgeGroupStatResponse DTO로 변환
     */
    private AgeGroupStatResponse convertToAgeGroupStatResponse(Map<String, Object> row) {
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
            throw new DataConversionException(ResponseCode.DTO_CONVERSION_FAILED);
        }
    }

    /**
     * 회원 ID 유효성 검증
     */
    private void validateMemberId(Long memberId) {
        if (memberId == null) {
            throw new ValidationException(ResponseCode.INVALID_MEMBER_ID);
        }
        if (memberId <= 0) {
            throw new ValidationException(ResponseCode.INVALID_MEMBER_ID);
        }
    }

    /**
     * 나이 유효성 검증
     */
    private void validateAge(Integer age) {
        if (age < 0 || age > 150) {
            throw new ValidationException(ResponseCode.INVALID_AGE_RANGE);
        }
    }

    /**
     * 자산 규모 유효성 검증
     */
    private void validateAssetAmount(Long totalAssets) {
        if (totalAssets < 0) {
            throw new ValidationException(ResponseCode.INVALID_ASSET_RANGE);
        }
    }

    /**
     * WMTI 코드 유효성 검증
     */
    private void validateWmtiCode(String wmtiCode) {
        if (wmtiCode.length() != 4) { // WMTI 코드가 4자리라고 가정
            throw new ValidationException(ResponseCode.INVALID_WMTI_CODE);

        }
    }

    /**
     * 나이 범위 계산
     */
    private AgeRange calculateAgeRange(int age) {
        int ageMin = (age / 10) * 10;
        int ageMax = ageMin + 9;
        return new AgeRange(ageMin, ageMax);
    }

    /**
     * 자산 범위 계산
     */
    private AssetRange calculateAssetRange(long totalAssets) {
        long amountMin = (totalAssets / 10000000) * 10000000;
        long amountMax = amountMin + 9999999;
        return new AssetRange(amountMin, amountMax);
    }

    /**
     * Object에서 double 값 추출
     */
    private double extractDoubleValue(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        return 0.0;
    }

    /**
     * 나이 범위를 나타내는 내부 클래스
     */
    private static class AgeRange {
        private final int min;
        private final int max;

        public AgeRange(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public int getMin() { return min; }
        public int getMax() { return max; }
    }

    /**
     * 자산 범위를 나타내는 내부 클래스
     */
    private static class AssetRange {
        private final long min;
        private final long max;

        public AssetRange(long min, long max) {
            this.min = min;
            this.max = max;
        }

        public long getMin() { return min; }
        public long getMax() { return max; }
    }
}