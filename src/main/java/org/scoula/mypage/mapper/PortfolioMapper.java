package org.scoula.mypage.mapper;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.mypage.dto.PortfolioCreateDTO;
import org.scoula.mypage.dto.PortfolioItemDTO;
import org.scoula.mypage.dto.PortfolioSummaryDTO;
import org.scoula.mypage.dto.PortfolioUpdateDTO;

import java.util.List;
import java.util.Map;


@Mapper
public interface PortfolioMapper {
    // 포트폴리오 아이템 목록 조회
    List<PortfolioItemDTO> getPortfolioItems(@Param("memberId") Long memberId);

    // 카테고리 및 서브카테고리 ID를 찾기
    Long findCategoryIdByProductId(@Param("productId") Long productId);
    Long findSubcategoryIdByProductId(@Param("productId") Long productId);

    // 포트폴리오 아이템 추가
    void insertPortfolioItem(@Param("memberId") Long memberId,
                             @Param("dto") PortfolioCreateDTO dto);


    // 포트폴리오 아이템 업데이트
    void updatePortfolioItem(@Param("portfolioId") Long portfolioId,
                             @Param("dto") PortfolioUpdateDTO dto);

    // 포트폴리오 아이템 삭제
    void deletePortfolioItem(@Param("portfolioId") Long portfolioId);

    // 포트폴리오 요약 정보 조회
    @MapKey("categoryName")
    List<Map<String, Object>> getPortfolioSummary(@Param("memberId") Long memberId);
}