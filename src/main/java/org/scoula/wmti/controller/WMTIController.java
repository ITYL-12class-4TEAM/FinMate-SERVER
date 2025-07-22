package org.scoula.wmti.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.scoula.wmti.dto.survey.WMTIRequestDTO;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.scoula.wmti.service.WMTIService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/submit")
    public ApiResponse<?> submitSurvey(@RequestBody WMTIRequestDTO wmtirequest Principal principal){

        List<Integer> answers = wmtirequest.getAnswers();
        if (answers == null || answers.size() != 20) {
            return ApiResponse.fail(ResponseCode.WMTI_INCOMPLETE_ANSWERS, "20개 문항이 모두 응답되어야 합니다.");
        }
        //TODO: 설문조사 답변처리 및 WMTI코드 계산
        String wmtiCode = wmtiService.calculateWMTICode(answers);
        Map<String, Object> response = new HashMap<>();
        response.put("wmtiCode", wmtiCode);
        response.put("message","검사완료");

        return ApiResponse.success(ResponseCode.WMTI_SURVEY_SUBMITTED,response);
    }
}
