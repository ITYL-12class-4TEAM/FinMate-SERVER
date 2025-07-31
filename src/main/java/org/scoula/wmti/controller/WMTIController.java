package org.scoula.wmti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.scoula.auth.exception.TokenValidationException;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.scoula.security.util.JwtProcessor;
import org.scoula.wmti.dto.survey.SurveyResultDTO;
import org.scoula.wmti.dto.survey.WMTIHistoryDTO;
import org.scoula.wmti.dto.survey.WMTIProfileDTO;
import org.scoula.wmti.entity.SurveyResult;
import org.scoula.wmti.service.WMTIService;

import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.scoula.wmti.dto.survey.SurveyRequestDTO;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Api(tags = "WMTI API", description = "WMTI 투자성향 조사용 API")
@RequestMapping("/api/wmti")
public class WMTIController {

    private final WMTIService wmtiService;
    private final JwtProcessor jwtProcessor;

    // 설문 제출 (POST)
    @ApiOperation(value = "설문 응답 제출", notes = "WMTI 20문항에 대한 응답을 제출하고 성향 코드 분석 결과를 저장합니다.")
    @PostMapping("/submit")
    public ApiResponse<?> submitSurvey(
            @ApiParam(value = "설문 응답 DTO", required = true)
            @RequestBody SurveyRequestDTO wmtirequest,
            HttpServletRequest request) {
        Long memberId = extractMemberIdFromRequest(request);
        SurveyResult saved = wmtiService.saveSurveyResult(memberId, wmtirequest.getAnswers());

        Map<String, Object> response = new HashMap<>();
        response.put("wmtiCode", saved.getWmtiCode());
        response.put("message", "검사완료");

        return ApiResponse.success(ResponseCode.WMTI_SURVEY_SUBMITTED, response);
    }

    // 설문 결과 조회 (GET)
    @ApiOperation(value = "최신 설문 결과 조회", notes = "회원의 최신 WMTI 성향 검사 결과를 조회합니다.")
    @GetMapping("result/{memberId}")
    public ApiResponse<SurveyResultDTO> getSurveyResult(
            @ApiParam(value = "회원 ID", required = true, example = "1")
            @PathVariable Long memberId,
            HttpServletRequest request
            ) {
        Long jwtMemberId = extractMemberIdFromRequest(request);
        if (!memberId.equals(jwtMemberId)) {
            return ApiResponse.fail(ResponseCode.AUTH_ACCESS_DENIED, "다른 사용자의 결과는 조회할 수 없습니다.");
        }
        SurveyResultDTO result = wmtiService.getSurveyResultByMemberId(memberId);
        return ApiResponse.success(ResponseCode.WMTI_SURVEY_RESULT_RETRIEVED, result);
    }

    //설문 이력 조회 (전체리스트 GET)
    @ApiOperation(value = "설문 이력 전체 조회", notes = "특정 회원의 WMTI 검사 이력을 모두 조회합니다.")
    @GetMapping("/history/member/{memberId}")
    public ApiResponse<List<WMTIHistoryDTO>> getSurveyHistory(
            @ApiParam(value = "회원 ID", required = true, example = "1")
            @PathVariable Long memberId
    ) {
        List<WMTIHistoryDTO> historyList = wmtiService.getSurveyHistoryByMemberId(memberId);
        return ApiResponse.success(ResponseCode.WMTI_HISTORY_RETRIEVED, historyList);
    }

    // 설문 이력 조회 (단일 이력, GET)
    @ApiOperation(value = "설문 이력 단건 조회", notes = "히스토리 ID를 기반으로 단일 WMTI 설문 이력을 조회합니다.")
    @GetMapping("/history/{historyId}")
    public ApiResponse<WMTIHistoryDTO> getSurveyHistoryByHistoryId(
            @ApiParam(value = "히스토리 ID", required = true, example = "10")
            @PathVariable Long historyId
    ) {
        WMTIHistoryDTO dto = wmtiService.getSurveyHistoryByHistoryId(historyId);
        return ApiResponse.success(ResponseCode.WMTI_HISTORY_RETRIEVED, dto);
    }

    // 성향 코드에 따른 분석 및 추천 상품 제공 (GET)
    @ApiOperation(value = "WMTI 코드 기반 분석 결과 조회", notes = "WMTI 성향 코드를 기반으로 분석 정보와 추천 결과를 제공합니다.")
    @GetMapping("/analysis/{wmtiCode}")
    public ApiResponse<WMTIProfileDTO> getAnalysis(
            @ApiParam(value = "WMTI 코드 (4자리)", required = true, example = "APML")
            @PathVariable String wmtiCode
    ) {
        WMTIProfileDTO result = wmtiService.getAnalysisByWMTICode(wmtiCode);
        return ApiResponse.success(ResponseCode.WMTI_ANALYSIS_SUCCESS, result);
    }
    // 모든 성향코드(16종)에 대한 분석 결과 조회 (GET)
    @ApiOperation(value = "전체 WMTI 성향 분석 목록 조회", notes = "16개의 WMTI 성향 코드에 대한 분석 정보를 모두 제공합니다.")
    @GetMapping("/analysis/all")
    public ApiResponse<List<WMTIProfileDTO>> getAllAnalyses() {
        List<WMTIProfileDTO> allProfiles = wmtiService.getAllAnalysisProfiles();
        return ApiResponse.success(ResponseCode.WMTI_ANALYSIS_SUCCESS, allProfiles);
    }
    //wmti설문문항 전달API
    @ApiOperation(value = "WMTI 설문 문항 조회", notes = "프론트에서 설문지를 렌더링할 수 있도록 20개의 WMTI 설문 문항을 반환합니다.")
    @GetMapping("/questions")
    public ApiResponse<?> getWMTIQuestions() {
        List<Map<String, Object>> questions = wmtiService.loadWMTIQuestions();
        return ApiResponse.success(ResponseCode.WMTI_QUESTION_RETRIEVED, questions);
    }
    // 토큰에서 memberId 추출메서드 (예외는 JwtProcessor에서 발생시키도록 함)
    private Long extractMemberIdFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new org.scoula.auth.exception.AuthenticationException(ResponseCode.AUTH_TOKEN_NOT_FOUND);
        }
        String token = header.substring(7);
        try {
            return jwtProcessor.getMemberId(token);
        } catch (ExpiredJwtException e) {
            throw new TokenValidationException(ResponseCode.AUTH_TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new TokenValidationException(ResponseCode.AUTH_TOKEN_INVALID);
        }
    }
}
