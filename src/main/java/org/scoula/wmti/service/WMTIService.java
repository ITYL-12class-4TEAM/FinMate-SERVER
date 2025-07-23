package org.scoula.wmti.service;

import org.scoula.wmti.dto.survey.SurveyResultDTO;
import org.scoula.wmti.dto.survey.WMTIHistoryDTO;
import org.scoula.wmti.entity.SurveyResult;

import java.util.List;

public interface WMTIService {
    /**
     * WMTI 코드를 계산하는 메서드
     *
     * @param answers 설문 응답
     * @return 계산된 WMTI 코드
     */
    String calculateWMTICode(List<Integer> answers);

    /**
     * 설문 결과를 DB에 저장하는 메서드
     *
     * @param memberId 사용자 고유 ID
     * @param answers 설문 응답
     * @return 저장된 설문 결과
     */
    SurveyResult saveSurveyResult(Long memberId, List<Integer> answers);
    // 설문 결과 조회
    SurveyResultDTO getSurveyResultByMemberId(Long memberId);
    // 설문 이력 조회
    List<WMTIHistoryDTO> getSurveyHistoryByMemberId(Long memberId);
    // 설문 이력 조회 (단일 이력)
    WMTIHistoryDTO getSurveyHistoryByHistoryId(Long historyId);
}
