package org.scoula.wmti.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.scoula.preinfo.entity.PreInformation;
import org.scoula.preinfo.mapper.PreInfoMapper;
import org.scoula.wmti.domain.WMTIAnalysis;
import org.scoula.wmti.domain.WMTICalculator;
import org.scoula.wmti.domain.WMTIScoreResult;
import org.scoula.wmti.dto.survey.SurveyResultDTO;
import org.scoula.wmti.dto.survey.WMTIHistoryDTO;
import org.scoula.wmti.dto.survey.WMTIProfileDTO;
import org.scoula.wmti.entity.SurveyResult;
import org.scoula.wmti.entity.WMTIHistory;
import org.scoula.wmti.enums.RiskPreference;
import org.scoula.wmti.enums.WMTIDimension;
import org.scoula.wmti.mapper.SurveyResultMapper;
import org.scoula.wmti.mapper.WMTIHistoryMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WMTIServiceImpl implements WMTIService {
    private final ObjectMapper objectMapper;  // Jackson Bean 자동 주입

    private final SurveyResultMapper surveyResultMapper;
    private final WMTICalculator wmtiCalculator;
    private final WMTIAnalysis wmtiAnalysis;
    private final WMTIHistoryMapper wmtiHistoryMapper;
    private final PreInfoMapper preInfoMapper;

    @Override
    public String calculateWMTICode(List<Integer> answers) {
        // WMTI 코드 계산
        return wmtiCalculator.calculateWMTICode(answers);
    }
    @Override
    public WMTIProfileDTO getAnalysisByWMTICode(String wmtiCode) {
        // WMTIAnalysis에서 처리된 분석 및 추천 상품 반환
        return wmtiAnalysis.getAnalysisByWMTICode(wmtiCode);
    }

    @Override
    public SurveyResult saveSurveyResult(Long memberId, List<Integer> answers) {
        String answersJson;
        try {
            answersJson = objectMapper.writeValueAsString(answers); // ← 진짜 JSON 배열로 직렬화
        } catch (Exception e) {
            throw new RuntimeException("JSON 직렬화 실패", e);
        }

        // 1. WMTI 코드 계산
        String wmtiCode = wmtiCalculator.calculateWMTICode(answers);

        // 2. 성향 점수 계산 및 코드 분리
        WMTIScoreResult scoreResult = wmtiCalculator.calculateScores(answers);
        double aScore = scoreResult.getAScore();
        double iScore = scoreResult.getIScore();
        double pScore = scoreResult.getPScore();
        double bScore = scoreResult.getBScore();
        double mScore = scoreResult.getMScore();
        double wScore = scoreResult.getWScore();
        double lScore = scoreResult.getLScore();
        double cScore = scoreResult.getCScore();

        // 3. 각 자리 문자 → WMTIDimension 변환
        WMTIDimension A = WMTIDimension.valueOf(wmtiCode.substring(0, 1));
        WMTIDimension P = WMTIDimension.valueOf(wmtiCode.substring(1, 2));
        WMTIDimension M = WMTIDimension.valueOf(wmtiCode.substring(2, 3));
        WMTIDimension L = WMTIDimension.valueOf(wmtiCode.substring(3, 4));

        // 4. 사전정보 연산값 불러오기(투자자유형, 리스크수용성, (+userName))
        PreInformation preInfo = preInfoMapper.findByMemberId(memberId); // (예시 메서드)
        String resultType = (preInfo != null) ? preInfo.getResultType() : null;
        RiskPreference riskPreference = (preInfo != null) ? preInfo.getRiskPreference() : null;
        String userName = (preInfo != null) ? preInfo.getUsername() : null;
        // 5. 설문 결과 DTO 객체 생성
        SurveyResultDTO surveyResultDTO = SurveyResultDTO.builder()
                .memberId(memberId)
                .wmtiCode(wmtiCode)
                .answersJson(answersJson)
                .aScore(aScore)
                .iScore(iScore)
                .pScore(pScore)
                .bScore(bScore)
                .mScore(mScore)
                .wScore(wScore)
                .lScore(lScore)
                .cScore(cScore)
                .A(A)
                .P(P)
                .M(M)
                .L(L)
                .resultType(resultType)
                .riskPreference(riskPreference)
                .userName(userName)
                .createdAt(LocalDateTime.now())
                .build();

        // 6. 설문 결과를 DB에 저장하기 전에 이전 결과를 wmti_history에 백업
        SurveyResult existingResult = surveyResultMapper.findByMemberId(memberId);
        backupSurveyResultToHistory(existingResult);

        // 7. SurveyResultDTO -> SurveyResult (Entity)로 변환
        SurveyResult surveyResult = SurveyResult.builder()
                .wmtiId(surveyResultDTO.getWmtiId())
                .memberId(surveyResultDTO.getMemberId())
                .resultType(resultType)
                .riskPreference(surveyResultDTO.getRiskPreference())
                .userName(userName)
                .answersJson(surveyResultDTO.getAnswersJson())
                .aScore(surveyResultDTO.getAScore())
                .iScore(surveyResultDTO.getIScore())
                .pScore(surveyResultDTO.getPScore())
                .bScore(surveyResultDTO.getBScore())
                .mScore(surveyResultDTO.getMScore())
                .wScore(surveyResultDTO.getWScore())
                .lScore(surveyResultDTO.getLScore())
                .cScore(surveyResultDTO.getCScore())
                .wmtiCode(surveyResultDTO.getWmtiCode())
                .a(surveyResultDTO.getA())
                .p(surveyResultDTO.getP())
                .m(surveyResultDTO.getM())
                .l(surveyResultDTO.getL())
                .createdAt(surveyResultDTO.getCreatedAt())
                .build();


        // 8. 설문결과 DTO를 DB에 저장 (기존 결과가 있으면 update, 없으면 insert)
        if (existingResult == null) {
            surveyResultMapper.saveSurveyResult(surveyResult); // insert
        } else {
            surveyResultMapper.updateSurveyResult(surveyResult); // update
        }


        return surveyResult;
    }
    // SurveyResult 백업 로직
    private void backupSurveyResultToHistory(SurveyResult existingResult) {

        if (existingResult != null) {
            // SurveyResult → WMTIHistory 변환
            WMTIHistory wmtiHistory = WMTIHistory.builder()
                    .memberId(existingResult.getMemberId())
                    .userName(existingResult.getUserName())
                    .wmtiCode(existingResult.getWmtiCode())
                    .answersJson(existingResult.getAnswersJson())
                    .createdAt(existingResult.getCreatedAt())

                    // 추가된 필드 반영
                    .resultType(existingResult.getResultType())
                    .riskPreference(existingResult.getRiskPreference())
                    .aScore(existingResult.getAScore())
                    .iScore(existingResult.getIScore())
                    .pScore(existingResult.getPScore())
                    .bScore(existingResult.getBScore())
                    .mScore(existingResult.getMScore())
                    .wScore(existingResult.getWScore())
                    .lScore(existingResult.getLScore())
                    .cScore(existingResult.getCScore())
                    .a(existingResult.getA())
                    .p(existingResult.getP())
                    .m(existingResult.getM())
                    .l(existingResult.getL())
                    .build();

            // WMTIHistory 엔티티를 wmti_history 테이블에 저장
            wmtiHistoryMapper.saveWMTIHistory(wmtiHistory);
        }
    }

    @Override
    public SurveyResultDTO getSurveyResultByMemberId(Long memberId) {
        // 1. SurveyResult 엔티티 조회
        SurveyResult surveyResult = surveyResultMapper.findByMemberId(memberId);

        if (surveyResult == null) {
            return null; // 설문 결과가 없으면 null 반환
        }

        // 2. Entity → DTO 변환 (Builder 사용)
        return SurveyResultDTO.builder()
                .wmtiId(surveyResult.getWmtiId())
                .memberId(surveyResult.getMemberId())
                .resultType(surveyResult.getResultType())
                .riskPreference(surveyResult.getRiskPreference())
                .userName(surveyResult.getUserName())
                .answersJson(surveyResult.getAnswersJson())
                .aScore(surveyResult.getAScore())
                .iScore(surveyResult.getIScore())
                .pScore(surveyResult.getPScore())
                .bScore(surveyResult.getBScore())
                .mScore(surveyResult.getMScore())
                .wScore(surveyResult.getWScore())
                .lScore(surveyResult.getLScore())
                .cScore(surveyResult.getCScore())
                .wmtiCode(surveyResult.getWmtiCode())
                .A(surveyResult.getA())
                .P(surveyResult.getP())
                .M(surveyResult.getM())
                .L(surveyResult.getL())
                .createdAt(surveyResult.getCreatedAt())
                .build();
    }

    @Override
    public List<WMTIHistoryDTO> getSurveyHistoryByMemberId(Long memberId) {
        // 1. 이력 목록 조회
        List<WMTIHistory> wmtiHistoryList = wmtiHistoryMapper.findAllByMemberId(memberId);

        if (wmtiHistoryList == null || wmtiHistoryList.isEmpty()) {
            return null;
        }

        // 2. Builder 패턴을 사용하여 DTO 변환
        return wmtiHistoryList.stream()
                .map(history -> WMTIHistoryDTO.builder()
                        .historyId(history.getHistoryId())
                        .wmtiCode(history.getWmtiCode())
                        .answersJson(history.getAnswersJson())
                        //.resultType(history.getResultType())
                        //.riskPreference(history.getRiskPreference())
                        .aScore(history.getAScore())
                        .pScore(history.getPScore())
                        .mScore(history.getMScore())
                        .lScore(history.getLScore())
                        .A(history.getA())
                        .P(history.getP())
                        .M(history.getM())
                        .L(history.getL())
                        .createdAt(history.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
    @Override
    public WMTIHistoryDTO getSurveyHistoryByHistoryId(Long historyId) {
        // 1. 단일 이력 조회
        WMTIHistory wmtiHistory = wmtiHistoryMapper.findByHistoryId(historyId);

        if (wmtiHistory == null) {
            return null; // 이력 없으면 null 반환
        }

        // 2. Builder를 사용해 DTO 변환
        return WMTIHistoryDTO.builder()
                .historyId(wmtiHistory.getHistoryId())
                .wmtiCode(wmtiHistory.getWmtiCode())
                .answersJson(wmtiHistory.getAnswersJson())
                //.resultType(wmtiHistory.getResultType())
                //.riskPreference(wmtiHistory.getRiskPreference())
                .aScore(wmtiHistory.getAScore())
                .pScore(wmtiHistory.getPScore())
                .mScore(wmtiHistory.getMScore())
                .lScore(wmtiHistory.getLScore())
                .A(wmtiHistory.getA())
                .P(wmtiHistory.getP())
                .M(wmtiHistory.getM())
                .L(wmtiHistory.getL())
                .createdAt(wmtiHistory.getCreatedAt())
                .build();
    }

    @Override
    public boolean deleteSurveyHistory(Long historyId) {
        //삭제 쿼리 실행
        int deletedRows = wmtiHistoryMapper.deleteByHistoryId(historyId);
        return deletedRows > 0; //삭제가 성공적으로 이루어졌으면 true반환
    }
}
