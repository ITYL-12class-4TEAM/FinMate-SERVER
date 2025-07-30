package org.scoula.wmti.util;

import org.scoula.wmti.dto.survey.SurveyResultDTO;
import org.scoula.wmti.entity.SurveyResult;
import org.scoula.wmti.entity.WMTIHistory;

public class SurveyResultMapperUtil {

    public static SurveyResult toEntity(SurveyResultDTO dto) {
        return SurveyResult.builder()
                .wmtiId(dto.getWmtiId())
                .memberId(dto.getMemberId())
                .userName(dto.getUserName())
                .resultType(dto.getResultType())
                .riskPreference(dto.getRiskPreference())
                .answersJson(dto.getAnswersJson())
                .aScore(dto.getAScore())
                .iScore(dto.getIScore())
                .pScore(dto.getPScore())
                .bScore(dto.getBScore())
                .mScore(dto.getMScore())
                .wScore(dto.getWScore())
                .lScore(dto.getLScore())
                .cScore(dto.getCScore())
                .wmtiCode(dto.getWmtiCode())
                .a(dto.getA())
                .p(dto.getP())
                .m(dto.getM())
                .l(dto.getL())
                .createdAt(dto.getCreatedAt())
                .build();
    }

    public static SurveyResultDTO toDTO(SurveyResult entity) {
        return SurveyResultDTO.builder()
                .wmtiId(entity.getWmtiId())
                .memberId(entity.getMemberId())
                .userName(entity.getUserName())
                .resultType(entity.getResultType())
                .riskPreference(entity.getRiskPreference())
                .answersJson(entity.getAnswersJson())
                .aScore(entity.getAScore())
                .iScore(entity.getIScore())
                .pScore(entity.getPScore())
                .bScore(entity.getBScore())
                .mScore(entity.getMScore())
                .wScore(entity.getWScore())
                .lScore(entity.getLScore())
                .cScore(entity.getCScore())
                .wmtiCode(entity.getWmtiCode())
                .A(entity.getA())
                .P(entity.getP())
                .M(entity.getM())
                .L(entity.getL())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static WMTIHistory toHistory(SurveyResult entity) {
        return WMTIHistory.builder()
                .memberId(entity.getMemberId())
                .userName(entity.getUserName())
                .wmtiCode(entity.getWmtiCode())
                .answersJson(entity.getAnswersJson())
                .createdAt(entity.getCreatedAt())
                .resultType(entity.getResultType())
                .riskPreference(entity.getRiskPreference())
                .aScore(entity.getAScore())
                .iScore(entity.getIScore())
                .pScore(entity.getPScore())
                .bScore(entity.getBScore())
                .mScore(entity.getMScore())
                .wScore(entity.getWScore())
                .lScore(entity.getLScore())
                .cScore(entity.getCScore())
                .a(entity.getA())
                .p(entity.getP())
                .m(entity.getM())
                .l(entity.getL())
                .build();
    }

    private SurveyResultMapperUtil() {}
}
