package org.scoula.mypage.recentView.service;

import lombok.RequiredArgsConstructor;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.mypage.favorite.mapper.ProductMapper;
import org.scoula.mypage.recentView.dto.RecentProductResponse;
import org.scoula.mypage.recentView.exception.ProductNotFoundException;
import org.scoula.mypage.recentView.exception.RecentViewNotFoundException;
import org.scoula.mypage.recentView.mapper.ViewedProductMapper;
import org.scoula.response.ResponseCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecentViewedServiceImpl implements RecentViewedService {
    private final ViewedProductMapper viewedProductMapper;
    private final MemberMapper memberMapper;
    private final ProductMapper productMapper;

    /**
     * 최근 본 상품 저장
     * @param productId 상품 ID
     * @param saveTrm 저축 기간
     * @param rsrvType 예약 타입
     */
    public void saveRecentView(Long productId, Integer saveTrm, String rsrvType) {
        Long memberId = getCurrentUserIdAsLong();

        // 입력값 검증
        if (productId == null) {
            throw new IllegalArgumentException("상품 ID는 필수입니다.");
        }

        // 상품 존재 여부 확인
         if (!productMapper.existsById(productId)) {
             throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
         }

        try {
            // 기존 기록이 있다면 삭제 후 새로 추가 (중복 방지 및 최신 순서 유지)
            viewedProductMapper.deleteExistingViewedProduct(memberId, productId, saveTrm, rsrvType);
            viewedProductMapper.insertViewedProduct(memberId, productId, saveTrm, rsrvType);
        } catch (Exception e) {
            throw new RuntimeException("최근 본 상품 저장 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 최근 본 상품 목록 조회
     * @return 최근 본 상품 목록
     */
    public List<RecentProductResponse> getRecentViews() {
        Long memberId = getCurrentUserIdAsLong();

        try {
            List<RecentProductResponse> recentViews = viewedProductMapper.selectRecentViewedProducts(memberId);

            // 빈 목록도 정상적인 결과로 처리 (예외 발생하지 않음)
            return recentViews;
        } catch (Exception e) {
            throw new RuntimeException("최근 본 상품 목록 조회 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 특정 상품의 최근 본 기록 삭제
     * @param productId 상품 ID
     */
    public void deleteRecentView(Long productId) {
        Long memberId = getCurrentUserIdAsLong();

        // 입력값 검증
        if (productId == null) {
            throw new IllegalArgumentException("상품 ID는 필수입니다.");
        }

        try {
            // 삭제할 기록이 있는지 확인
            boolean exists = viewedProductMapper.existsRecentView(memberId, productId);
            if (!exists) {
                throw new RecentViewNotFoundException(ResponseCode.RECENT_VIEW_NOT_FOUND);
            }

            int deletedCount = viewedProductMapper.deleteViewedProduct(memberId, productId);

            // 실제로 삭제된 레코드가 없다면 예외 발생
            if (deletedCount == 0) {
                throw new RecentViewNotFoundException(ResponseCode.RECENT_VIEW_NOT_FOUND);
            }
        } catch (RecentViewNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("최근 본 상품 삭제 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 모든 최근 본 상품 기록 삭제
     */
    public void deleteAllRecentViews() {
        Long memberId = getCurrentUserIdAsLong();

        try {
            // 삭제할 기록이 있는지 확인
            List<RecentProductResponse> existingViews = viewedProductMapper.selectRecentViewedProducts(memberId);
            if (existingViews.isEmpty()) {
                throw new RecentViewNotFoundException(ResponseCode.RECENT_VIEW_NOT_FOUND);
            }

            int deletedCount = viewedProductMapper.deleteAllViewedProducts(memberId);

            // 실제로 삭제된 레코드가 없다면 예외 발생
            if (deletedCount == 0) {
                throw new RecentViewNotFoundException(ResponseCode.RECENT_VIEW_NOT_FOUND);
            }
        } catch (RecentViewNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("모든 최근 본 상품 삭제 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 현재 로그인한 사용자의 ID를 Long 타입으로 반환
     * @return 사용자 ID
     */
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