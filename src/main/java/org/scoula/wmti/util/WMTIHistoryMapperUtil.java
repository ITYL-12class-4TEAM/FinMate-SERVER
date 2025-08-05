package org.scoula.wmti.util;

import org.scoula.wmti.dto.survey.WMTIHistoryDTO;
import org.scoula.wmti.entity.WMTIHistory;

public class WMTIHistoryMapperUtil {

    public static WMTIHistoryDTO toDTO(WMTIHistory entity) {
        if (entity == null) return null;

        return WMTIHistoryDTO.builder()
                .historyId(entity.getHistoryId())
                .wmtiCode(entity.getWmtiCode())
                .answersJson(entity.getAnswersJson())
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
                .A(entity.getA())
                .P(entity.getP())
                .M(entity.getM())
                .L(entity.getL())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private WMTIHistoryMapperUtil() {}
}

