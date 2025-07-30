package org.scoula.wmti.util;

import org.scoula.preinfo.enums.InvestmentType;
import org.scoula.wmti.dto.survey.SurveyResultDTO;
import org.scoula.wmti.entity.SurveyResult;
import org.scoula.wmti.entity.WMTIHistory;
import org.scoula.wmti.enums.RiskPreference;
import org.scoula.wmti.enums.WMTIDimension;

import java.time.LocalDateTime;

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

    public static SurveyResultDTO fromRawData(
            Long memberId,
            String userName,
            String wmtiCode,
            String answersJson,
            double aScore, double iScore,
            double pScore, double bScore,
            double mScore, double wScore,
            double lScore, double cScore,
            WMTIDimension A, WMTIDimension P, WMTIDimension M, WMTIDimension L, // ✅ 타입 수정
            InvestmentType resultType,
            RiskPreference riskPreference
    ) {
        return SurveyResultDTO.builder()
                .memberId(memberId)
                .userName(userName)
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
                .createdAt(LocalDateTime.now())
                .build();
    }
    public static WMTIDimension[] parseWMTIDimensions(String code) {
        if (code == null || code.length() != 4) {
            throw new IllegalArgumentException("유효하지 않은 WMTI 코드입니다.");
        }

        return new WMTIDimension[] {
                WMTIDimension.valueOf(code.substring(0, 1)),
                WMTIDimension.valueOf(code.substring(1, 2)),
                WMTIDimension.valueOf(code.substring(2, 3)),
                WMTIDimension.valueOf(code.substring(3, 4))
        };
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
