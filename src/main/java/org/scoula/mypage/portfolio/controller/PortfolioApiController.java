package org.scoula.mypage.portfolio.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.scoula.mypage.portfolio.dto.PortfolioCreateRequest;
import org.scoula.mypage.portfolio.dto.PortfolioItemResponse;
import org.scoula.mypage.portfolio.dto.PortfolioSummaryWithComparisonResponse;
import org.scoula.mypage.portfolio.dto.PortfolioUpdateRequest;
import org.scoula.mypage.portfolio.service.PortfolioService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "포트폴리오 API")
@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioApiController {

    private final PortfolioService portfolioService;

    @ApiOperation(value = "포트폴리오 목록 조회", notes = "로그인한 사용자의 포트폴리오 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<List<PortfolioItemResponse>> getPortfolio() {
        List<PortfolioItemResponse> result = portfolioService.getPortfolioList();
        return ApiResponse.success(ResponseCode.PORTFOLIO_READ_SUCCESS, result);
    }

    @ApiOperation(value = "포트폴리오 추가", notes = "포트폴리오 항목을 새로 추가합니다.")
    @PostMapping
    public ApiResponse<Void> addPortfolio(
            @ApiParam(value = "포트폴리오 생성 정보", required = true)
            @Valid @RequestBody PortfolioCreateRequest dto) {
        portfolioService.addPortfolio(dto);
        return ApiResponse.success(ResponseCode.PORTFOLIO_CREATE_SUCCESS);
    }

    @ApiOperation(value = "포트폴리오 수정", notes = "포트폴리오 항목을 수정합니다.")
    @PatchMapping("/{portfolioId}")
    public ApiResponse<Void> updatePortfolio(
            @ApiParam(value = "포트폴리오 ID", required = true)
            @PathVariable Long portfolioId,
            @ApiParam(value = "수정할 포트폴리오 정보", required = true)
            @Valid @RequestBody PortfolioUpdateRequest dto) {
        portfolioService.updatePortfolio(portfolioId, dto);
        return ApiResponse.success(ResponseCode.PORTFOLIO_UPDATE_SUCCESS);
    }

    @ApiOperation(value = "포트폴리오 삭제", notes = "포트폴리오 항목을 삭제합니다.")
    @DeleteMapping("/{portfolioId}")
    public ApiResponse<Void> deletePortfolio(
            @ApiParam(value = "포트폴리오 ID", required = true)
            @PathVariable Long portfolioId) {
        portfolioService.deletePortfolio(portfolioId);
        return ApiResponse.success(ResponseCode.PORTFOLIO_DELETE_SUCCESS);
    }

    @ApiOperation(
            value = "포트폴리오 요약 + 비교 통계 조회",
            notes = "로그인한 사용자의 포트폴리오 통계와 비교 통계(연령대, 금액대, WMTI)를 함께 조회합니다."
    )
    @GetMapping("/summary")
    public ApiResponse<PortfolioSummaryWithComparisonResponse> getSummary() {
        PortfolioSummaryWithComparisonResponse result = portfolioService.getSummaryWithComparison();
        return ApiResponse.success(ResponseCode.PORTFOLIO_SUMMARY_SUCCESS, result);
    }
}
