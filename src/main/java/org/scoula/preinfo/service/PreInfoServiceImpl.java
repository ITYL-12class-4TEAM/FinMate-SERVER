package org.scoula.preinfo.service;

import lombok.RequiredArgsConstructor;
import org.scoula.preinfo.domain.PreInfoCalculator;
import org.scoula.preinfo.dto.PreInfoRequestDTO;
import org.scoula.preinfo.dto.PreInfoResponseDTO;
import org.scoula.preinfo.entity.PreInformation;
import org.scoula.preinfo.enums.InvestmentCapacity;
import org.scoula.preinfo.enums.InvestmentType;
import org.scoula.preinfo.mapper.PreInfoMapper;
import org.scoula.preinfo.util.PreInfoMapperUtil;
import org.scoula.security.util.JwtProcessor;
import org.scoula.wmti.enums.RiskPreference;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PreInfoServiceImpl implements PreInfoService {
    private final PreInfoMapper preInfoMapper;
    private final JwtProcessor jwtUtil; // 토큰 생성기
    private final PreInfoCalculator calculator;

    //입력된 사전정보 조회 by memberId
    @Override
    public PreInformation getUserProfile(Long memberId) {
        return preInfoMapper.findByMemberId(memberId);
    }

    //입력된 사전정보 조회 by preInfoId
    @Override
    public PreInformation getPreInfoById(String preInfoId) {
        return preInfoMapper.findByPreInfoId(preInfoId);
    }

    //사전정보 등록/저장.
    @Override
    public PreInfoResponseDTO savePreInfoAndResponse(Long userId, PreInfoRequestDTO dto) {
        //현재시간 저장
        LocalDateTime now = LocalDateTime.now();

        //입력값 -> 산출값 계산 : PreInfoCalculator
        long surplus = calculator.calculateSurplusAmount(dto.getMonthlyIncome(), dto.getFixedCost());
        int savingsRate = calculator.calculateSavingsRate(dto.getMonthlyIncome(), surplus);
        int score = calculator.calculateFinancialHealthScore(savingsRate);
        InvestmentCapacity investmentCapacity = calculator.calculateInvestmentCapacity(savingsRate, surplus, dto.getMonthlyIncome(), dto.getAge(), dto.getMarried());
        RiskPreference riskPreference = calculator.calculateRiskPreference(dto, surplus, savingsRate);
        InvestmentType resultType = calculator.calculateResultType(score);
        long recommended = calculator.calculateRecommendedMonthlyInvestment(dto.getPeriod(), riskPreference, surplus);


        //고유 ID + 토큰생성
        String preInfoId = generatePreInfoId(userId, now);
        String token = jwtUtil.generateSurveyToken(userId); // JwtUtil 직접 구현 필요

        //엔티티 구성
        PreInformation profile = PreInfoMapperUtil.toEntity(
                preInfoId, userId, dto, surplus, savingsRate, score,
                recommended, investmentCapacity, riskPreference, resultType, now
        );

        // DB 저장 -> insert/update 분기
        PreInformation existing = preInfoMapper.findByMemberId(userId);
        if (existing == null) {
            preInfoMapper.insertPreInfo(profile);
        } else {
            preInfoMapper.updatePreInfo(profile);
        }

        // DTO 로 응답 구성
        return PreInfoMapperUtil.toResponseDTO(
                preInfoId, userId, token, now,
                surplus, savingsRate, score, recommended,
                investmentCapacity, resultType, riskPreference
        );
    }

    //입력양식에 따른 PREINFO_ID 명명
    private String generatePreInfoId(Long userId, LocalDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return "PRE_" + userId + "_" + timestamp.format(formatter);
    }
}
