package org.scoula.wmti.controller;

import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.scoula.wmti.dto.survey.SurveyResultDTO;
import org.scoula.wmti.dto.survey.WMTIHistoryDTO;
import org.scoula.wmti.service.WMTIService;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.scoula.wmti.dto.survey.SurveyRequestDTO;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Api(tags = "WMTI API", description = "WMTI 투자성향 조사용 API")
@RequestMapping("/api/wmti")
public class WMTIController {

    private final WMTIService wmtiService;

    // 설문 제출 (POST)
    @PostMapping("/submit")
    public ApiResponse<?> submitSurvey(@RequestBody SurveyRequestDTO wmtirequest, Principal principal) {
        // 20개 문항 응답 여부 체크
        if (!wmtirequest.isValid()) {
            return ApiResponse.fail(ResponseCode.WMTI_INCOMPLETE_ANSWERS, "20개 문항이 모두 응답되어야 합니다.");
        }

        // 설문 응답에 대한 WMTI 코드 계산
        String wmtiCode = wmtiService.calculateWMTICode(wmtirequest.getAnswers());

        // 응답 결과 및 메시지를 담을 Map 객체 생성
        Map<String, Object> response = new HashMap<>();
        response.put("wmtiCode", wmtiCode);
        response.put("message", "검사완료");

        // 성공적인 설문 제출 응답 반환
        return ApiResponse.success(ResponseCode.WMTI_SURVEY_SUBMITTED, response);
    }

    // 설문 결과 조회 (GET)
    @GetMapping("/{memberId}")
    public ApiResponse<SurveyResultDTO> getSurveyResult(@PathVariable Long memberId) {
        SurveyResultDTO surveyResultDTO = wmtiService.getSurveyResultByMemberId(memberId);

        if (surveyResultDTO == null) {
            return ApiResponse.fail(ResponseCode.WMTI_SURVEY_PROCESSING_FAILED, "설문 결과가 없습니다.");
        }

        // ApiResponse로 응답 반환
        return ApiResponse.success(ResponseCode.WMTI_SURVEY_RESULT_RETRIEVED, surveyResultDTO);
    }
    //설문 이력 조회 (전체리스트 GET)
    public ApiResponse<List<WMTIHistoryDTO>> getSurveyHistory(@PathVariable Long memberId) {
        List<WMTIHistoryDTO> wmtiHistoryDTOList = wmtiService.getSurveyHistoryByMemberId(memberId);
        if (wmtiHistoryDTOList == null || wmtiHistoryDTOList.isEmpty()) {
            return ApiResponse.fail(ResponseCode.WMTI_SURVEY_PROCESSING_FAILED, "설문 이력이 없습니다.");
        }
        return ApiResponse.success(ResponseCode.WMTI_SURVEY_RESULT_RETRIEVED, wmtiHistoryDTOList);
    }
    // 설문 이력 조회 (단일 이력, GET)
    @GetMapping("/history/{historyId}")
    public ApiResponse<WMTIHistoryDTO> getSurveyHistoryByHistoryId(@PathVariable Long historyId) {
        WMTIHistoryDTO wmtiHistoryDTO = wmtiService.getSurveyHistoryByHistoryId(historyId);

        if (wmtiHistoryDTO == null) {
            return ApiResponse.fail(ResponseCode.WMTI_SURVEY_PROCESSING_FAILED, "설문 이력을 찾을 수 없습니다.");
        }

        return ApiResponse.success(ResponseCode.WMTI_SURVEY_RESULT_RETRIEVED, wmtiHistoryDTO);
    }
    // 성향 코드에 따른 분석 및 추천 상품 제공 (GET)
    @GetMapping("/analysis/{wmtiCode}")
    public ApiResponse<Map<String, Object>> getAnalysis(@PathVariable String wmtiCode) {
        Map<String, Object> analysisResult = wmtiService.getAnalysisByWMTICode(wmtiCode);

        if (analysisResult == null || analysisResult.isEmpty()) {
            return ApiResponse.fail(ResponseCode.미정, "분석 결과를 찾을 수 없습니다.");
        }

        return ApiResponse.success(ResponseCode.미정, analysisResult);
    }
}
