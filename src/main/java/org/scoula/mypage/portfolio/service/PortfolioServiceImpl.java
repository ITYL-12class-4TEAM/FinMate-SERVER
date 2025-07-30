package org.scoula.mypage.portfolio.service;

import lombok.RequiredArgsConstructor;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.mypage.portfolio.dto.*;
import org.scoula.mypage.portfolio.exception.PortfolioAccessDeniedException;
import org.scoula.mypage.portfolio.exception.PortfolioNotFoundException;
import org.scoula.mypage.portfolio.exception.ProductNotFoundException;
import org.scoula.mypage.portfolio.mapper.PortfolioMapper;
import org.scoula.response.ResponseCode;
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

        try {
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
        } catch (Exception e) {
            throw new RuntimeException("포트폴리오 목록 조회 중 오류가 발생했습니다.", e);
        }
    }

    // 포트폴리오 추가
    public void addPortfolio(PortfolioCreateRequest dto) {
        Long memberId = getCurrentUserIdAsLong();

        // 입력값 검증
        if (dto.getProductId() == null) {
            throw new IllegalArgumentException("상품 ID는 필수입니다.");
        }
        if (dto.getAmount() == null || dto.getAmount() <= 0) {
            throw new IllegalArgumentException("가입 금액은 0보다 커야 합니다.");
        }
        if (dto.getSaveTrm() == null || dto.getSaveTrm() <= 0) {
            throw new IllegalArgumentException("저축 기간은 0보다 커야 합니다.");
        }

        try {
            // 상품 존재 여부 확인
            Long categoryId = portfolioMapper.findCategoryIdByProductId(dto.getProductId());
            if (categoryId == null) {
                throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
            }

            Long subcategoryId = portfolioMapper.findSubcategoryIdByProductId(dto.getProductId());
            Long optionId = portfolioMapper.findOptionIdByProductIdAndTerm(dto.getProductId(), dto.getSaveTrm());

            dto.setCategoryId(categoryId);
            dto.setSubcategoryId(subcategoryId);

            portfolioMapper.insertPortfolioItem(memberId, dto);
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("포트폴리오 추가 중 오류가 발생했습니다.", e);
        }
    }

    // 포트폴리오 수정
    public void updatePortfolio(Long portfolioId, PortfolioUpdateRequest dto) {
        Long memberId = getCurrentUserIdAsLong();

        // 포트폴리오 존재 여부 및 소유권 확인
        if (!portfolioMapper.existsById(portfolioId)) {
            throw new PortfolioNotFoundException(ResponseCode.PORTFOLIO_NOT_FOUND);
        }

        if (!portfolioMapper.isOwner(portfolioId, memberId)) {
            throw new PortfolioAccessDeniedException(ResponseCode.PORTFOLIO_ACCESS_DENIED);
        }

        // 입력값 검증
        if (dto.getAmount() != null && dto.getAmount() <= 0) {
            throw new IllegalArgumentException("가입 금액은 0보다 커야 합니다.");
        }

        try {
            portfolioMapper.updatePortfolioItem(portfolioId, dto);
        } catch (Exception e) {
            throw new RuntimeException("포트폴리오 수정 중 오류가 발생했습니다.", e);
        }
    }

    // 포트폴리오 삭제
    public void deletePortfolio(Long portfolioId) {
        Long memberId = getCurrentUserIdAsLong();

        // 포트폴리오 존재 여부 및 소유권 확인
        if (!portfolioMapper.existsById(portfolioId)) {
            throw new PortfolioNotFoundException(ResponseCode.PORTFOLIO_NOT_FOUND);
        }

        if (!portfolioMapper.isOwner(portfolioId, memberId)) {
            throw new PortfolioAccessDeniedException(ResponseCode.PORTFOLIO_ACCESS_DENIED);
        }

        try {
            portfolioMapper.deletePortfolioItem(portfolioId);
        } catch (Exception e) {
            throw new RuntimeException("포트폴리오 삭제 중 오류가 발생했습니다.", e);
        }
    }

    // 포트폴리오 요약 정보 조회 + 비교 통계
    public PortfolioSummaryWithComparisonResponse getSummaryWithComparison() {
        Long memberId = getCurrentUserIdAsLong();

        try {
            List<PortfolioSummaryResponse> mySummary = getMyPortfolioSummary(memberId);
            PortfolioComparisonResponse comparison = comparisonService.getComparisonStats(memberId);

            PortfolioSummaryWithComparisonResponse response = new PortfolioSummaryWithComparisonResponse();
            response.setMySummary(mySummary);
            response.setComparisonSummary(comparison);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("포트폴리오 요약 조회 중 오류가 발생했습니다.", e);
        }
    }

    // 개인 포트폴리오 요약 정보 조회
    private List<PortfolioSummaryResponse> getMyPortfolioSummary(Long memberId) {
        try {
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

            // 비율 계산 시 0으로 나누기 방지
            if (total > 0) {
                for (PortfolioSummaryResponse dto : grouped.values()) {
                    dto.setRatio((dto.getTotalAmount() * 100.0) / total);
                    for (SubcategorySummaryResponse sub : dto.getSubcategories()) {
                        sub.setRatio((sub.getTotalAmount() * 100.0) / dto.getTotalAmount());
                    }
                }
            }

            return new ArrayList<>(grouped.values());
        } catch (Exception e) {
            throw new RuntimeException("포트폴리오 요약 데이터 처리 중 오류가 발생했습니다.", e);
        }
    }

    // 현재 로그인한 사용자의 ID를 Long 타입으로 반환
    private Long getCurrentUserIdAsLong() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Long memberId = memberMapper.getMemberIdByEmail(email);

            if (memberId == null) {
                throw new RuntimeException("사용자 정보를 찾을 수 없습니다.");
            }

            return memberId;
        } catch (Exception e) {
            throw new RuntimeException("사용자 인증 정보를 가져오는데 실패했습니다.", e);
        }
    }
}