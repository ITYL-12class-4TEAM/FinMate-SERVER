package org.scoula.wmti.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
        // 20개 문항 응답 여부 체크
        if (!wmtirequest.isValid()) {
            return ApiResponse.fail(ResponseCode.WMTI_INCOMPLETE_ANSWERS, "20개 문항이 모두 응답되어야 합니다.");
        }
        //JWT기반 인증에서 memberId 따오기
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return ApiResponse.fail(ResponseCode.AUTH_TOKEN_NOT_FOUND);
        }
        String token = header.substring(7); // "Bearer " 제거

        Long memberId;
        try {
            memberId = jwtProcessor.getMemberId(token);
        } catch (ExpiredJwtException e) {
            return ApiResponse.fail(ResponseCode.AUTH_TOKEN_EXPIRED);
        } catch (JwtException e) {
            return ApiResponse.fail(ResponseCode.AUTH_TOKEN_INVALID);
        }

        try {
            // 설문 응답에 대한 WMTI 코드 계산 + 저장
            SurveyResult saved = wmtiService.saveSurveyResult(memberId, wmtirequest.getAnswers());
            // 응답 구성 (Map객체)
            Map<String, Object> response = new HashMap<>();
            response.put("wmtiCode", saved.getWmtiCode());
            response.put("message", "검사완료");

            return ApiResponse.success(ResponseCode.WMTI_SURVEY_SUBMITTED, response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(ResponseCode.WMTI_INVALID_ANSWER_FORMAT, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(ResponseCode.WMTI_SAVE_FAILED, "설문 결과 저장 중 서버 오류가 발생했습니다.");
        }
    }

    // 설문 결과 조회 (GET)
    @ApiOperation(value = "최신 설문 결과 조회", notes = "회원의 최신 WMTI 성향 검사 결과를 조회합니다.")
    @GetMapping("/{memberId}")
    public ApiResponse<SurveyResultDTO> getSurveyResult(
            @ApiParam(value = "회원 ID", required = true, example = "1")
            @PathVariable Long memberId,
            HttpServletRequest request
            ) {
        // 1. JWT에서 memberId 추출
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return ApiResponse.fail(ResponseCode.AUTH_TOKEN_NOT_FOUND);
        }
        String token = header.substring(7);

        Long jwtMemberId;
        try {
            jwtMemberId = jwtProcessor.getMemberId(token);
        } catch (ExpiredJwtException e) {
            return ApiResponse.fail(ResponseCode.AUTH_TOKEN_EXPIRED);
        } catch (JwtException e) {
            return ApiResponse.fail(ResponseCode.AUTH_TOKEN_INVALID);
        }
        if (!memberId.equals(jwtMemberId)) {
            return ApiResponse.fail(ResponseCode.AUTH_ACCESS_DENIED, "다른 사용자의 결과는 조회할 수 없습니다.");
        }
        try {
            SurveyResultDTO surveyResultDTO = wmtiService.getSurveyResultByMemberId(memberId);

            if (surveyResultDTO == null) {
                return ApiResponse.fail(ResponseCode.WMTI_RESULT_NOT_FOUND, "설문조사를 아직 시행한적 없습니다.");
            }

            return ApiResponse.success(ResponseCode.WMTI_SURVEY_RESULT_RETRIEVED, surveyResultDTO);
        } catch (Exception e) {
            return ApiResponse.fail(ResponseCode.WMTI_RESULT_RETRIEVAL_FAILED, "서버 오류로 결과를 조회하지 못했습니다.");
        }
    }

    //설문 이력 조회 (전체리스트 GET)
    @ApiOperation(value = "설문 이력 전체 조회", notes = "특정 회원의 WMTI 검사 이력을 모두 조회합니다.")
    @GetMapping("/history/member/{memberId}")
    public ApiResponse<List<WMTIHistoryDTO>> getSurveyHistory(
            @ApiParam(value = "회원 ID", required = true, example = "1")
            @PathVariable Long memberId
    ) {
        try {
            List<WMTIHistoryDTO> historyList = wmtiService.getSurveyHistoryByMemberId(memberId);
            if (historyList == null || historyList.isEmpty()) {
                return ApiResponse.fail(ResponseCode.WMTI_HISTORY_NOT_FOUND, "설문 이력이 존재하지 않습니다.");
            }
            return ApiResponse.success(ResponseCode.WMTI_HISTORY_RETRIEVED, historyList);
        } catch (Exception e) {
            return ApiResponse.fail(ResponseCode.WMTI_HISTORY_RETRIEVAL_FAILED, "설문 이력 조회 중 오류가 발생했습니다.");
        }
    }

    // 설문 이력 조회 (단일 이력, GET)
    @ApiOperation(value = "설문 이력 단건 조회", notes = "히스토리 ID를 기반으로 단일 WMTI 설문 이력을 조회합니다.")
    @GetMapping("/history/{historyId}")
    public ApiResponse<WMTIHistoryDTO> getSurveyHistoryByHistoryId(
            @ApiParam(value = "히스토리 ID", required = true, example = "10")
            @PathVariable Long historyId
    ) {
        try {
            WMTIHistoryDTO dto = wmtiService.getSurveyHistoryByHistoryId(historyId);
            if (dto == null) {
                return ApiResponse.fail(ResponseCode.WMTI_HISTORY_NOT_FOUND, "설문 이력을 찾을 수 없습니다.");
            }
            return ApiResponse.success(ResponseCode.WMTI_HISTORY_RETRIEVED, dto);
        } catch (Exception e) {
            return ApiResponse.fail(ResponseCode.WMTI_HISTORY_RETRIEVAL_FAILED, "설문 이력 조회 중 오류가 발생했습니다.");
        }
    }

    // 성향 코드에 따른 분석 및 추천 상품 제공 (GET)
    @ApiOperation(value = "WMTI 코드 기반 분석 결과 조회", notes = "WMTI 성향 코드를 기반으로 분석 정보와 추천 결과를 제공합니다.")
    @GetMapping("/analysis/{wmtiCode}")
    public ApiResponse<WMTIProfileDTO> getAnalysis(
            @ApiParam(value = "WMTI 코드 (4자리)", required = true, example = "APML")
            @PathVariable String wmtiCode
    ) {
        try {
            WMTIProfileDTO analysisResult = wmtiService.getAnalysisByWMTICode(wmtiCode);
            if (analysisResult == null) {
                return ApiResponse.fail(ResponseCode.WMTI_ANALYSIS_NOT_FOUND, "분석 결과를 찾을 수 없습니다.");
            }
            return ApiResponse.success(ResponseCode.WMTI_ANALYSIS_SUCCESS, analysisResult);
        } catch (Exception e) {
            return ApiResponse.fail(ResponseCode.WMTI_ANALYSIS_FAILED, "분석 결과 처리 중 오류가 발생했습니다.");
        }
    }
}
