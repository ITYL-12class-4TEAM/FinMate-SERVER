package org.scoula.mypage.portfolio.service;

import lombok.RequiredArgsConstructor;
import org.scoula.mypage.favorite.exception.ValidationException;
import org.scoula.mypage.util.SecurityUtil;
import org.scoula.mypage.portfolio.dto.*;
import org.scoula.mypage.portfolio.exception.*;
import org.scoula.mypage.portfolio.mapper.PortfolioMapper;
import org.scoula.response.ResponseCode;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {
    private final PortfolioMapper portfolioMapper;
    private final ComparisonService comparisonService;
    private final SecurityUtil securityUtil;

    // 포트폴리오 목록 조회
    public List<PortfolioItemResponse> getPortfolioList() {
        Long memberId = securityUtil.getCurrentUserIdAsLong();

        try {
            List<PortfolioItemResponse> items = portfolioMapper.getPortfolioItems(memberId);

            if (items != null) {
                // 각 포트폴리오 아이템에 대해 이자 계산 및 만기일 설정
                processPortfolioItems(items);
            }

            return items != null ? items : List.of();
        } catch (Exception e) {
            throw new PortfolioServiceException(ResponseCode.PORTFOLIO_READ_FAILED);
        }
    }

    // 포트폴리오 추가
    public void addPortfolio(PortfolioCreateRequest dto) {
        Long memberId = securityUtil.getCurrentUserIdAsLong();

        // 입력값 검증
        validatePortfolioCreateRequest(dto);

        try {
            // 상품 정보 조회 및 검증
            PortfolioProductInfo productInfo = getAndValidateProductInfo(dto.getProductId(), dto.getSaveTrm());

            // DTO에 카테고리 정보 설정
            dto.setCategoryId(productInfo.getCategoryId());
            dto.setSubcategoryId(productInfo.getSubcategoryId());

            portfolioMapper.insertPortfolioItem(memberId, dto);
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException(ResponseCode.DATABASE_ERROR);
        }
    }

    // 포트폴리오 수정
    public void updatePortfolio(Long portfolioId, PortfolioUpdateRequest dto) {
        Long memberId = securityUtil.getCurrentUserIdAsLong();

        // 포트폴리오 존재 여부 및 소유권 확인
        validatePortfolioOwnership(portfolioId, memberId);

        // 입력값 검증
        validatePortfolioUpdateRequest(dto);

        try {
            portfolioMapper.updatePortfolioItem(portfolioId, dto);
        } catch (Exception e) {
            throw new DatabaseOperationException(ResponseCode.DATABASE_ERROR);
        }
    }

    // 포트폴리오 삭제
    public void deletePortfolio(Long portfolioId) {
        Long memberId = securityUtil.getCurrentUserIdAsLong();

        // 포트폴리오 존재 여부 및 소유권 확인
        validatePortfolioOwnership(portfolioId, memberId);

        try {
            portfolioMapper.deletePortfolioItem(portfolioId);
        } catch (Exception e) {
            throw new DatabaseOperationException(ResponseCode.DATABASE_ERROR);
        }
    }

    // 포트폴리오 요약 정보 조회 + 비교 통계
    public PortfolioSummaryWithComparisonResponse getSummaryWithComparison() {
        Long memberId = securityUtil.getCurrentUserIdAsLong();

        try {
            List<PortfolioSummaryResponse> mySummary = getMyPortfolioSummary(memberId);
            PortfolioComparisonResponse comparison = comparisonService.getComparisonStats(memberId);

            PortfolioSummaryWithComparisonResponse response = new PortfolioSummaryWithComparisonResponse();
            response.setMySummary(mySummary);
            response.setComparisonSummary(comparison);
            return response;
        } catch (Exception e) {
            throw new PortfolioServiceException(ResponseCode.PORTFOLIO_SUMMARY_FAILED);
        }
    }

    /**
     * 포트폴리오 아이템들에 대한 이자 계산 및 만기일 처리
     */
    private void processPortfolioItems(List<PortfolioItemResponse> items) {
        for (PortfolioItemResponse item : items) {
            try {
                if (item.getSaveTerm() != null && item.getCustomRate() != null) {
                    calculateInterestAndMaturity(item);
                }
            } catch (Exception e) {
                throw new PortfolioCalculationException(ResponseCode.PORTFOLIO_CALCULATION_FAILED);
            }
        }
    }

    /**
     * 이자 계산 및 만기일 설정
     */
    private void calculateInterestAndMaturity(PortfolioItemResponse item) {
        try {
            double rate = item.getCustomRate() / 100.0;
            double periodInYear = item.getSaveTerm() / 12.0;
            double interest = item.getAmount() * rate * periodInYear;

            // 세금 15.4% 계산
            double tax = interest * 0.154;
            double netProfit = interest - tax;

            item.setEstimatedInterest((long) interest);
            item.setEstimatedAfterTax(item.getAmount() + (long) netProfit);

            // 만기일 계산
            if (item.getJoinDate() != null) {
                LocalDate join = LocalDate.parse(item.getJoinDate());
                LocalDate maturity = join.plusMonths(item.getSaveTerm());
                item.setMaturityDate(maturity.toString());
            }
        } catch (DateTimeParseException e) {
            throw new PortfolioCalculationException(ResponseCode.INVALID_DATE_FORMAT);
        } catch (ArithmeticException e) {
            throw new PortfolioCalculationException(ResponseCode.CALCULATION_ERROR);
        }
    }

    /**
     * 개인 포트폴리오 요약 정보 조회
     */
    private List<PortfolioSummaryResponse> getMyPortfolioSummary(Long memberId) {
        try {
            List<Map<String, Object>> rawData = portfolioMapper.getPortfolioSummary(memberId);

            if (rawData == null || rawData.isEmpty()) {
                return List.of();
            }

            return processPortfolioSummaryData(rawData);
        } catch (Exception e) {
            throw new PortfolioServiceException(ResponseCode.PORTFOLIO_SUMMARY_PROCESSING_FAILED);
        }
    }

    /**
     * 포트폴리오 요약 데이터 처리
     */
    private List<PortfolioSummaryResponse> processPortfolioSummaryData(List<Map<String, Object>> rawData) {
        Map<String, PortfolioSummaryResponse> grouped = new LinkedHashMap<>();
        long total = 0;

        // 데이터 그루핑 및 합계 계산
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

        // 비율 계산
        calculateRatios(grouped, total);

        return new ArrayList<>(grouped.values());
    }

    /**
     * 비율 계산 (0으로 나누기 방지)
     */
    private void calculateRatios(Map<String, PortfolioSummaryResponse> grouped, long total) {
        if (total > 0) {
            for (PortfolioSummaryResponse dto : grouped.values()) {
                dto.setRatio((dto.getTotalAmount() * 100.0) / total);
                for (SubcategorySummaryResponse sub : dto.getSubcategories()) {
                    sub.setRatio((sub.getTotalAmount() * 100.0) / dto.getTotalAmount());
                }
            }
        }
    }

    /**
     * 포트폴리오 생성 요청 입력값 검증
     */
    private void validatePortfolioCreateRequest(PortfolioCreateRequest dto) {
        if (dto.getProductId() == null) {
            throw new IllegalArgumentException("상품 ID는 필수입니다.");
        }
        if (dto.getAmount() == null || dto.getAmount() <= 0) {
            throw new IllegalArgumentException("가입 금액은 0보다 커야 합니다.");
        }
        if (dto.getSaveTrm() == null || dto.getSaveTrm() <= 0) {
            throw new IllegalArgumentException("저축 기간은 0보다 커야 합니다.");
        }
    }

    /**
     * 포트폴리오 수정 요청 입력값 검증
     */
    private void validatePortfolioUpdateRequest(PortfolioUpdateRequest dto) {
        if (dto.getAmount() != null && dto.getAmount() <= 0) {
            throw new ValidationException(ResponseCode.INVALID_AMOUNT);
        }
    }
    /**
     * 포트폴리오 소유권 검증
     */
    private void validatePortfolioOwnership(Long portfolioId, Long memberId) {
        if (!portfolioMapper.existsById(portfolioId)) {
            throw new PortfolioNotFoundException(ResponseCode.PORTFOLIO_NOT_FOUND);
        }

        if (!portfolioMapper.isOwner(portfolioId, memberId)) {
            throw new PortfolioAccessDeniedException(ResponseCode.PORTFOLIO_ACCESS_DENIED);
        }
    }

    /**
     * 상품 정보 조회 및 검증
     */
    private PortfolioProductInfo getAndValidateProductInfo(Long productId, Integer saveTrm) {
        try {
            Long categoryId = portfolioMapper.findCategoryIdByProductId(productId);
            if (categoryId == null) {
                throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
            }

            Long subcategoryId = portfolioMapper.findSubcategoryIdByProductId(productId);
            Long optionId = portfolioMapper.findOptionIdByProductIdAndTerm(productId, saveTrm);

            return new PortfolioProductInfo(categoryId, subcategoryId, optionId);
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException(ResponseCode.DATABASE_ERROR);
        }
    }

    /**
     * 상품 정보를 담는 내부 클래스
     */
    private static class PortfolioProductInfo {
        private final Long categoryId;
        private final Long subcategoryId;
        private final Long optionId;

        public PortfolioProductInfo(Long categoryId, Long subcategoryId, Long optionId) {
            this.categoryId = categoryId;
            this.subcategoryId = subcategoryId;
            this.optionId = optionId;
        }

        public Long getCategoryId() { return categoryId; }
        public Long getSubcategoryId() { return subcategoryId; }
        public Long getOptionId() { return optionId; }
    }
}