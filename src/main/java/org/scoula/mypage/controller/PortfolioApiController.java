package org.scoula.mypage.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.scoula.mypage.dto.PortfolioCreateDTO;
import org.scoula.mypage.dto.PortfolioItemDTO;
import org.scoula.mypage.dto.PortfolioSummaryDTO;
import org.scoula.mypage.dto.PortfolioUpdateDTO;
import org.scoula.mypage.service.PortfolioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "포트폴리오 API")
@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioApiController {

    private final PortfolioService portfolioService;

    private Long getLoginMemberId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @ApiOperation(value = "포트폴리오 목록 조회", notes = "로그인한 사용자의 포트폴리오 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<PortfolioItemDTO>> getPortfolio() {
        return ResponseEntity.ok(portfolioService.getPortfolioList(getLoginMemberId()));
    }

    @ApiOperation(value = "포트폴리오 추가", notes = "포트폴리오 항목을 새로 추가합니다.")
    @PostMapping
    public ResponseEntity<Void> addPortfolio(
            @ApiParam(value = "포트폴리오 생성 정보", required = true) @RequestBody PortfolioCreateDTO dto) {
        portfolioService.addPortfolio(getLoginMemberId(), dto);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "포트폴리오 수정", notes = "포트폴리오 항목을 수정합니다.")
    @PatchMapping("/{portfolioId}")
    public ResponseEntity<Void> updatePortfolio(
            @ApiParam(value = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @ApiParam(value = "수정할 포트폴리오 정보", required = true) @RequestBody PortfolioUpdateDTO dto) {
        portfolioService.updatePortfolio(portfolioId, dto);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "포트폴리오 삭제", notes = "포트폴리오 항목을 삭제합니다.")
    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<Void> deletePortfolio(
            @ApiParam(value = "포트폴리오 ID", required = true) @PathVariable Long portfolioId) {
        portfolioService.deletePortfolio(portfolioId);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "포트폴리오 요약 조회", notes = "로그인한 사용자의 포트폴리오 통계 정보를 조회합니다.")
    @GetMapping("/summary")
    public ResponseEntity<List<PortfolioSummaryDTO>> getSummary() {
        return ResponseEntity.ok(portfolioService.getSummary(getLoginMemberId()));
    }
}
