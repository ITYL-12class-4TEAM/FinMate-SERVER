package org.scoula.mypage.recentView.service;

import lombok.RequiredArgsConstructor;
import org.scoula.mypage.favorite.exception.ValidationException;
import org.scoula.mypage.util.SecurityUtil;
import org.scoula.mypage.favorite.mapper.ProductMapper;
import org.scoula.mypage.recentView.dto.RecentProductResponse;
import org.scoula.mypage.recentView.exception.DatabaseOperationException;
import org.scoula.mypage.recentView.exception.ProductNotFoundException;
import org.scoula.mypage.recentView.exception.RecentViewNotFoundException;
import org.scoula.mypage.recentView.exception.RecentViewServiceException;
import org.scoula.mypage.recentView.mapper.ViewedProductMapper;
import org.scoula.response.ResponseCode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecentViewedServiceImpl implements RecentViewedService {
    private final ViewedProductMapper viewedProductMapper;
    private final ProductMapper productMapper;
    private final SecurityUtil securityUtil;

    /**
     * 최근 본 상품 저장
     * @param productId 상품 ID
     * @param saveTrm 저축 기간
     * @param rsrvType 예약 타입
     */
    public void saveRecentView(Long productId, Integer saveTrm,String intrRateType ,String rsrvType) {
        Long memberId = securityUtil.getCurrentUserIdAsLong();

        // 입력값 검증
        validateProductId(productId);

        // 상품 존재 여부 확인
        if (!productMapper.existsById(productId)) {
            throw new ProductNotFoundException(ResponseCode.PRODUCT_NOT_FOUND);
        }

        try {
            // 기존 기록이 있다면 삭제 후 새로 추가 (중복 방지 및 최신 순서 유지)
            viewedProductMapper.deleteExistingViewedProduct(memberId, productId, saveTrm, intrRateType, rsrvType);
            viewedProductMapper.insertViewedProduct(memberId, productId, saveTrm,intrRateType, rsrvType);
        } catch (Exception e) {
            throw new DatabaseOperationException(ResponseCode.DATABASE_ERROR);
        }
    }

    /**
     * 최근 본 상품 목록 조회
     * @return 최근 본 상품 목록
     */
    public List<RecentProductResponse> getRecentViews() {
        Long memberId = securityUtil.getCurrentUserIdAsLong();

        try {
            List<RecentProductResponse> recentViews = viewedProductMapper.selectRecentViewedProducts(memberId);

            // 빈 목록도 정상적인 결과로 처리 (예외 발생하지 않음)
            return recentViews != null ? recentViews : List.of();
        } catch (Exception e) {
            throw new RecentViewServiceException(ResponseCode.RECENT_VIEW_READ_FAILED);
        }
    }

    /**
     * 특정 상품의 최근 본 기록 삭제
     * @param productId 상품 ID
     */
    public void deleteRecentView(Long productId) {
        Long memberId = securityUtil.getCurrentUserIdAsLong();

        // 입력값 검증
        validateProductId(productId);

        // 삭제할 기록이 있는지 사전 확인
        validateRecentViewExists(memberId, productId);

        try {
            int deletedCount = viewedProductMapper.deleteViewedProduct(memberId, productId);

            // 실제로 삭제된 레코드가 없다면 예외 발생
            if (deletedCount == 0) {
                throw new RecentViewNotFoundException(ResponseCode.RECENT_VIEW_NOT_FOUND);
            }
        } catch (RecentViewNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException(ResponseCode.DATABASE_ERROR);
        }
    }

    /**
     * 모든 최근 본 상품 기록 삭제
     */
    public void deleteAllRecentViews() {
        Long memberId = securityUtil.getCurrentUserIdAsLong();

        // 삭제할 기록이 있는지 사전 확인
        validateHasRecentViews(memberId);

        try {
            int deletedCount = viewedProductMapper.deleteAllViewedProducts(memberId);

            // 실제로 삭제된 레코드가 없다면 예외 발생
            if (deletedCount == 0) {
                throw new RecentViewNotFoundException(ResponseCode.RECENT_VIEW_NOT_FOUND);
            }
        } catch (RecentViewNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException(ResponseCode.DATABASE_ERROR);
        }
    }

    /**
     * 상품 ID 유효성 검증
     */
    private void validateProductId(Long productId) {
        if (productId == null) {
            throw new ValidationException(ResponseCode.INVALID_PRODUCT_ID);
        }
        if (productId <= 0) {
            throw new ValidationException(ResponseCode.INVALID_PRODUCT_ID);
        }
    }

    /**
     * 최근 본 상품 기록 존재 여부 검증
     */
    private void validateRecentViewExists(Long memberId, Long productId) {
        try {
            boolean exists = viewedProductMapper.existsRecentView(memberId, productId);
            if (!exists) {
                throw new RecentViewNotFoundException(ResponseCode.RECENT_VIEW_NOT_FOUND);
            }
        } catch (RecentViewNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException(ResponseCode.DATABASE_ERROR);
        }
    }

    /**
     * 삭제할 최근 본 상품 기록이 있는지 검증
     */
    private void validateHasRecentViews(Long memberId) {
        try {
            List<RecentProductResponse> existingViews = viewedProductMapper.selectRecentViewedProducts(memberId);
            if (existingViews == null || existingViews.isEmpty()) {
                throw new RecentViewNotFoundException(ResponseCode.RECENT_VIEW_NOT_FOUND);
            }
        } catch (RecentViewNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException(ResponseCode.DATABASE_ERROR);
        }
    }
}