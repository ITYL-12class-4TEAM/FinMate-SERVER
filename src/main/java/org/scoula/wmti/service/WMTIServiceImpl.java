package org.scoula.wmti.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.scoula.wmti.domain.WMTIAnalysis;
import org.scoula.wmti.domain.WMTICalculator;
import org.scoula.wmti.dto.survey.SurveyResultDTO;
import org.scoula.wmti.dto.survey.WMTIHistoryDTO;
import org.scoula.wmti.entity.SurveyResult;
import org.scoula.wmti.entity.WMTIHistory;
import org.scoula.wmti.enums.RiskPreference;
import org.scoula.wmti.enums.WMTIDimension;
import org.scoula.wmti.mapper.SurveyResultMapper;
import org.scoula.wmti.mapper.WMTIHistoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WMTIServiceImpl implements WMTIService {

    private final SurveyResultMapper surveyResultMapper;
    private final WMTICalculator wmtiCalculator;
    private final WMTIAnalysis wmtiAnalysis;
    private final WMTIHistoryMapper wmtiHistoryMapper;
    private final ObjectMapper objectMapper;  // Jackson ObjectMapper

    @Override
    public String calculateWMTICode(List<Integer> answers) {
        // WMTI 코드 계산
        return wmtiCalculator.calculateWMTICode(answers);
    }

    @Override
    public SurveyResult saveSurveyResult(Long memberId, List<Integer> answers) {
        // WMTI 코드 계산
        String wmtiCode = calculateWMTICode(answers);

        // 설문 결과 DTO 객체 생성
        SurveyResultDTO surveyResultDTO = new SurveyResultDTO();
        surveyResultDTO.setMemberId(memberId);
        surveyResultDTO.setWmtiCode(wmtiCode);
        surveyResultDTO.setAnswersJson(answers.toString());
        surveyResultDTO.setCreatedAt(java.time.LocalDateTime.now());

        // 설문 결과를 DB에 저장하기 전에 이전 결과를 wmti_history에 백업
        backupSurveyResultToHistory(memberId);

        // SurveyResultDTO -> SurveyResult (Entity)로 변환
        SurveyResult surveyResult = new SurveyResult();
        surveyResult.setMemberId(surveyResultDTO.getMemberId());
        surveyResult.setWmtiCode(surveyResultDTO.getWmtiCode());
        surveyResult.setAnswersJson(surveyResultDTO.getAnswersJson());
        surveyResult.setCreatedAt(surveyResultDTO.getCreatedAt());

        // 설문결과 DTO를 DB에 저장
        surveyResultMapper.saveSurveyResult(surveyResult);

        return surveyResult;
    }
    // SurveyResult 백업 로직
    private void backupSurveyResultToHistory(Long memberId) {
        // 기존 설문 결과 조회
        SurveyResult existingResult = surveyResultMapper.findByMemberId(memberId);

        if (existingResult != null) {
            // SurveyResult -> WMTIHistory 엔티티로 변환
            WMTIHistory wmtiHistory = new WMTIHistory();
            wmtiHistory.setMemberId(existingResult.getMemberId());
            wmtiHistory.setWmtiCode(existingResult.getWmtiCode());
            wmtiHistory.setAnswersJson(existingResult.getAnswersJson());
            wmtiHistory.setCreatedAt(existingResult.getCreatedAt());

            // 성향 분석 (가장 최근 설문 결과에서 성향을 추출하여 설정)
            wmtiHistory.setA(WMTIDimension.valueOf(existingResult.getWmtiCode().substring(0, 1))); // A/I 성향
            wmtiHistory.setP(WMTIDimension.valueOf(existingResult.getWmtiCode().substring(1, 2))); // P/B 성향
            wmtiHistory.setM(WMTIDimension.valueOf(existingResult.getWmtiCode().substring(2, 3))); // M/W 성향
            wmtiHistory.setL(WMTIDimension.valueOf(existingResult.getWmtiCode().substring(3, 4))); // L/C 성향

            // 위험 성향 (여기서는 간단히 예시로 설정)
            // 실제 로직은 성향에 따라 분류하는 방식으로 변경 가능
            wmtiHistory.setRiskPreference(RiskPreference.valueOf("적극투자형")); // 예시

            // WMTIHistory 엔티티를 wmti_history 테이블에 저장
            wmtiHistoryMapper.saveWMTIHistory(wmtiHistory);
        }
    }

    @Override
    public SurveyResultDTO getSurveyResultByMemberId(Long memberId) {
        //SurveyResult 엔티티 조회
        SurveyResult surveyResult = surveyResultMapper.findByMemberId(memberId);
        if (surveyResult == null) {
            return null; //설문결과가 없으면 null 반환
        }

        // SurveyResult -> SurveyResultDTO 변환
        SurveyResultDTO surveyResultDTO = new SurveyResultDTO();
        surveyResultDTO.setWmtiCode(surveyResult.getWmtiCode());
        surveyResultDTO.setAnswersJson(surveyResult.getAnswersJson());
        surveyResultDTO.setCreatedAt(surveyResult.getCreatedAt());

        return surveyResultDTO;
    }

    @Override
    public List<WMTIHistoryDTO> getSurveyHistoryByMemberId(Long memberId) {
        // WMTIHistory 이력 조회 (전체이력)
        List<WMTIHistory> wmtiHistoryList = wmtiHistoryMapper.findAllByMemberId(memberId);

        if (wmtiHistoryList == null || wmtiHistoryList.isEmpty()) {
            return null;// 설문 이력이 없으면 null 반환
        }

        return wmtiHistoryList.stream()
                .map(wmtiHistory -> {
                    WMTIHistoryDTO wmtiHistoryDTO = new WMTIHistoryDTO();
                    wmtiHistoryDTO.setHistoryId(wmtiHistory.getHistoryId());
                    wmtiHistoryDTO.setWmtiCode(wmtiHistory.getWmtiCode());
                    wmtiHistoryDTO.setAnswersJson(wmtiHistory.getAnswersJson());
                    wmtiHistoryDTO.setResultType(wmtiHistory.getResultType());
                    wmtiHistoryDTO.setA(wmtiHistory.getA());
                    wmtiHistoryDTO.setP(wmtiHistory.getP());
                    wmtiHistoryDTO.setM(wmtiHistory.getM());
                    wmtiHistoryDTO.setL(wmtiHistory.getL());
                    wmtiHistoryDTO.setRiskPreference(wmtiHistory.getRiskPreference());
                    wmtiHistoryDTO.setCreatedAt(wmtiHistory.getCreatedAt()); // createdAt 추가
                    return wmtiHistoryDTO;
                })
                .collect(Collectors.toList());
    }
    @Override
    public WMTIHistoryDTO getSurveyHistoryByHistoryId(Long historyId) {
        // WMTIHistory 이력 조회 (단일 이력)
        WMTIHistory wmtiHistory = wmtiHistoryMapper.findByHistoryId(historyId);

        if (wmtiHistory == null) {
            return null; // 이력 없으면 null 반환
        }

        // WMTIHistory -> WMTIHistoryDTO 변환
        WMTIHistoryDTO wmtiHistoryDTO = new WMTIHistoryDTO();
        wmtiHistoryDTO.setHistoryId(wmtiHistory.getHistoryId());
        wmtiHistoryDTO.setWmtiCode(wmtiHistory.getWmtiCode());
        wmtiHistoryDTO.setAnswersJson(wmtiHistory.getAnswersJson());
        wmtiHistoryDTO.setResultType(wmtiHistory.getResultType());
        wmtiHistoryDTO.setA(wmtiHistory.getA());
        wmtiHistoryDTO.setP(wmtiHistory.getP());
        wmtiHistoryDTO.setM(wmtiHistory.getM());
        wmtiHistoryDTO.setL(wmtiHistory.getL());
        wmtiHistoryDTO.setRiskPreference(wmtiHistory.getRiskPreference());
        wmtiHistoryDTO.setCreatedAt(wmtiHistory.getCreatedAt()); // createdAt 추가

        return wmtiHistoryDTO;
    }

    @Override
    public boolean deleteSurveyHistory(Long historyId) {
        //삭제 쿼리 실행
        int deletedRows = wmtiHistoryMapper.deleteByHistoryId(historyId);
        return deletedRows > 0; //삭제가 성공적으로 이루어졌으면 true반환
    }
}
