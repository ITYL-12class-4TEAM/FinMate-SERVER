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
import java.util.*;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {
    private final PortfolioMapper portfolioMapper;
    private final ComparisonService comparisonService;
    private final SecurityUtil securityUtil;

    /**
     * 포트폴리오 목록 조회
     * @return 포트폴리오 아이템 목록
     */
    public List<PortfolioItemResponse> getPortfolioList() {
        Long memberId = securityUtil.getCurrentUserIdAsLong();
        try {
            List<PortfolioItemResponse> items = portfolioMapper.getPortfolioItems(memberId);
            if (items != null) {
                processPortfolioItems(items);
            }
            return items != null ? items : List.of();
        } catch (Exception e) {
            throw new PortfolioServiceException(ResponseCode.PORTFOLIO_READ_FAILED);
        }
    }

    /**
     * 포트폴리오 추가
     * @param dto 포트폴리오 생성 요청 DTO
     */
    public void addPortfolio(PortfolioCreateRequest dto) {
        Long memberId = securityUtil.getCurrentUserIdAsLong();
        validatePortfolioCreateRequest(dto);

        try {
            portfolioMapper.insertPortfolioItem(memberId, dto);
        } catch (Exception e) {
            throw new DatabaseOperationException(ResponseCode.DATABASE_ERROR);
        }
    }

    /**
     * 포트폴리오 수정
     * @param portfolioId 포트폴리오 ID
     * @param dto 포트폴리오 수정 요청 DTO
     */
    public void updatePortfolio(Long portfolioId, PortfolioUpdateRequest dto) {
        Long memberId = securityUtil.getCurrentUserIdAsLong();
        validatePortfolioOwnership(portfolioId, memberId);
        validatePortfolioUpdateRequest(dto);

        try {
            portfolioMapper.updatePortfolioItem(portfolioId, dto);
        } catch (Exception e) {
            throw new DatabaseOperationException(ResponseCode.DATABASE_ERROR);
        }
    }

    /**
     * 포트폴리오 삭제
     * @param portfolioId 포트폴리오 ID
     */
    public void deletePortfolio(Long portfolioId) {
        Long memberId = securityUtil.getCurrentUserIdAsLong();
        validatePortfolioOwnership(portfolioId, memberId);

        try {
            portfolioMapper.deletePortfolioItem(portfolioId);
        } catch (Exception e) {
            throw new DatabaseOperationException(ResponseCode.DATABASE_ERROR);
        }
    }

    /**
     * 포트폴리오 요약 및 비교 통계 조회
     * @return 포트폴리오 요약 및 비교 통계 응답 DTO
     */
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
     * 포트폴리오 아이템의 이자 및 만기일 계산
     * @param items 포트폴리오 아이템 목록
     */
    private void processPortfolioItems(List<PortfolioItemResponse> items) {
        for (PortfolioItemResponse item : items) {
            try {
                if (item.getSaveTrm() != null && item.getCustomRate() != null) {
                    calculateInterestAndMaturity(item);
                }
            } catch (Exception e) {
                throw new PortfolioCalculationException(ResponseCode.PORTFOLIO_CALCULATION_FAILED);
            }
        }
    }


    /**
     * 포트폴리오 아이템의 이자 및 만기일 계산
     * @param item 포트폴리오 아이템
     */
    private void calculateInterestAndMaturity(PortfolioItemResponse item) {
        try {
            double rate = item.getCustomRate() / 100.0;
            double periodInYear = item.getSaveTrm() / 12.0;
            double interest = item.getAmount() * rate * periodInYear;

            double tax = interest * 0.154;
            double netProfit = interest - tax;

            item.setEstimatedInterest((long) interest);
            item.setEstimatedAfterTax(item.getAmount() + (long) netProfit);

            if (item.getJoinDate() != null) {
                LocalDate join = LocalDate.parse(item.getJoinDate());
                LocalDate maturity = join.plusMonths(item.getSaveTrm());
                item.setMaturityDate(maturity.toString());
            }
        } catch (DateTimeParseException e) {
            throw new PortfolioCalculationException(ResponseCode.INVALID_DATE_FORMAT);
        } catch (ArithmeticException e) {
            throw new PortfolioCalculationException(ResponseCode.CALCULATION_ERROR);
        }
    }

    /**
     * 내 포트폴리오 요약 조회
     * @param memberId 회원 ID
     * @return 포트폴리오 요약 응답 DTO 목록
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
     * @param rawData 포트폴리오 요약 데이터
     * @return 포트폴리오 요약 응답 DTO 목록
     */
    private List<PortfolioSummaryResponse> processPortfolioSummaryData(List<Map<String, Object>> rawData) {
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

        calculateRatios(grouped, total);
        return new ArrayList<>(grouped.values());
    }

    /**
     * 카테고리 및 소분류의 비율 계산
     * @param grouped 카테고리별 포트폴리오 요약 응답 DTO 맵
     * @param total 전체 금액
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
     * 포트폴리오 생성 요청 DTO 유효성 검증
     * @param dto 포트폴리오 생성 요청 DTO
     */
    private void validatePortfolioCreateRequest(PortfolioCreateRequest dto) {
        if (dto.getAmount() == null || dto.getAmount() <= 0) {
            throw new ValidationException(ResponseCode.INVALID_AMOUNT);
        }
        if (dto.getJoinDate() == null || dto.getJoinDate().isBlank()) {
            throw new ValidationException(ResponseCode.INVALID_DATE_FORMAT);
        }
        if (dto.getCategory() == null || dto.getSubcategory() == null) {
            throw new ValidationException(ResponseCode.INVALID_CATEGORY_OR_SUBCATEGORY);
        }
    }

    /**
     * 포트폴리오 수정 요청 DTO 유효성 검증
     * @param dto 포트폴리오 수정 요청 DTO
     */
    private void validatePortfolioUpdateRequest(PortfolioUpdateRequest dto) {
        if (dto.getAmount() != null && dto.getAmount() <= 0) {
            throw new ValidationException(ResponseCode.INVALID_AMOUNT);
        }
    }

    /**
     * 포트폴리오 소유권 및 접근 권한 검증
     * @param portfolioId 포트폴리오 ID
     * @param memberId 회원 ID
     */
    private void validatePortfolioOwnership(Long portfolioId, Long memberId) {
        if (!portfolioMapper.existsById(portfolioId)) {
            throw new PortfolioNotFoundException(ResponseCode.PORTFOLIO_NOT_FOUND);
        }

        if (!portfolioMapper.isOwner(portfolioId, memberId)) {
            throw new PortfolioAccessDeniedException(ResponseCode.PORTFOLIO_ACCESS_DENIED);
        }
    }
}
