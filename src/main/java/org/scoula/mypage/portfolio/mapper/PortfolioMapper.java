package org.scoula.mypage.portfolio.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.MapKey;
import org.scoula.mypage.portfolio.dto.PortfolioCreateRequest;
import org.scoula.mypage.portfolio.dto.PortfolioItemResponse;
import org.scoula.mypage.portfolio.dto.PortfolioUpdateRequest;

import java.util.List;
import java.util.Map;

@Mapper
public interface PortfolioMapper {

    // 포트폴리오 아이템 목록 조회
    List<PortfolioItemResponse> getPortfolioItems(@Param("memberId") Long memberId);

    // 포트폴리오 아이템 추가
    void insertPortfolioItem(@Param("memberId") Long memberId,
                             @Param("dto") PortfolioCreateRequest dto);

    // 포트폴리오 아이템 업데이트
    void updatePortfolioItem(@Param("portfolioId") Long portfolioId,
                             @Param("dto") PortfolioUpdateRequest dto);

    // 포트폴리오 아이템 삭제
    void deletePortfolioItem(@Param("portfolioId") Long portfolioId);

    // 포트폴리오 요약 정보 조회
    @MapKey("categoryName")
    List<Map<String, Object>> getPortfolioSummary(@Param("memberId") Long memberId);

    // 같은 나이대 사용자들의 카테고리별 평균 비율
    List<Map<String, Object>> getAgeGroupComparison(@Param("memberId") Long memberId,
                                                    @Param("ageMin") int ageMin,
                                                    @Param("ageMax") int ageMax);

    // 같은 자산 규모 사용자들의 카테고리별 평균 비율
    List<Map<String, Object>> getAmountGroupComparison(@Param("memberId") Long memberId,
                                                       @Param("amountMin") long amountMin,
                                                       @Param("amountMax") long amountMax);

    // 같은 WMTI 사용자들의 카테고리별 평균 비율
    List<Map<String, Object>> getWMTIComparison(@Param("memberId") Long memberId,
                                                @Param("wmtiCode") String wmtiCode);

    // 사용자의 총 자산 조회 (자산 그룹 분류용)
    Long getTotalAssetsByMemberId(@Param("memberId") Long memberId);

    // 사용자의 WMTI 코드 조회
    String getWmtiCodeByMemberId(@Param("memberId") Long memberId);

    // 사용자의 나이 조회 (나이대 분류용)
    Integer getAgeByMemberId(@Param("memberId") Long memberId);

    // 포트폴리오 멤버 수를 나이대별로 조회
    List<Map<String, Object>> getPortfolioMemberCountByAgeGroup();

    // 포트폴리오 존재 여부
    boolean existsById(Long portfolioId);

    // 포트폴리오 소유권 확인
    boolean isOwner(@Param("portfolioId") Long portfolioId, @Param("memberId") Long memberId);
}
