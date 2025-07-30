package org.scoula.preinfo.service;

import lombok.RequiredArgsConstructor;
import org.scoula.preinfo.domain.PreInfoCalculator;
import org.scoula.preinfo.dto.PreInfoRequestDTO;
import org.scoula.preinfo.dto.PreInfoResponseDTO;
import org.scoula.preinfo.entity.PreInformation;
import org.scoula.preinfo.enums.InvestmentCapacity;
import org.scoula.preinfo.enums.InvestmentType;
import org.scoula.preinfo.exception.PreInfoAnalysisException;
import org.scoula.preinfo.exception.PreInfoDuplicateException;
import org.scoula.preinfo.exception.PreInfoIllegalAccessException;
import org.scoula.preinfo.exception.PreInfoNotFoundException;
import org.scoula.preinfo.mapper.PreInfoMapper;
import org.scoula.preinfo.util.PreInfoMapperUtil;
import org.scoula.response.ResponseCode;
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
        PreInformation info = preInfoMapper.findByMemberId(memberId);
        if (info == null) {
            throw new PreInfoNotFoundException(ResponseCode.PREINFO_NOT_FOUND);
        }
        return info;
    }

    //입력된 사전정보 조회 by preInfoId
    @Override
    public PreInformation getPreInfoById(String preInfoId) {
        PreInformation info = preInfoMapper.findByPreInfoId(preInfoId);
        if(info == null) {
            throw new PreInfoNotFoundException(ResponseCode.PREINFO_NOT_FOUND);
        }
        return info;
    }

    //사전정보 등록/저장.
    @Override
    public PreInfoResponseDTO savePreInfoAndResponse(Long userId, PreInfoRequestDTO dto) {

        if (userId == null) {
            throw new PreInfoIllegalAccessException(ResponseCode.PREINFO_ILLEGAL_REQUEST);
        }

        //현재시간 저장
        LocalDateTime now = LocalDateTime.now();

        // ===== 산출값 계산 단계 (예외 감지 포함) ===== : PreInfoCalculator
        long surplus = calculator.calculateSurplusAmount(dto.getMonthlyIncome(), dto.getFixedCost());
        if (surplus < 0) {
            throw new PreInfoAnalysisException(ResponseCode.PREINFO_ANALYSIS_FAILED);
        }

        int savingsRate = calculator.calculateSavingsRate(dto.getMonthlyIncome(), surplus);
        int score = calculator.calculateFinancialHealthScore(savingsRate);

        InvestmentCapacity investmentCapacity;
        RiskPreference riskPreference;
        InvestmentType resultType;
        long recommended;

        try {
            investmentCapacity = calculator.calculateInvestmentCapacity(savingsRate, surplus, dto.getMonthlyIncome(), dto.getAge(), dto.getMarried());
            riskPreference = calculator.calculateRiskPreference(dto, surplus, savingsRate);
            resultType = calculator.calculateResultType(score);
            recommended = calculator.calculateRecommendedMonthlyInvestment(dto.getPeriod(), riskPreference, surplus);
        } catch (Exception e) {
            throw new PreInfoAnalysisException(ResponseCode.PREINFO_ANALYSIS_FAILED);
        }

        if (recommended <= 0) {
            throw new PreInfoAnalysisException(ResponseCode.PREINFO_ANALYSIS_FAILED);
        }

        // ===== 중복 입력 방지 확인 =====
        PreInformation existing = preInfoMapper.findByMemberId(userId);
        if (existing != null && existing.equalsInput(dto)) {
            throw new PreInfoDuplicateException(ResponseCode.PREINFO_ALREADY_EXISTS);
        }

        // ===== 고유 ID 및 토큰 생성 =====
        String preInfoId = (existing == null)
                ? generatePreInfoId(userId, now)
                : existing.getPreInfoId();
        String token = jwtUtil.generateSurveyToken(userId);


        // ===== 엔티티 구성 및 저장 =====
        PreInformation profile = PreInfoMapperUtil.toEntity(
                preInfoId, userId, dto, surplus, savingsRate, score,
                recommended, investmentCapacity, riskPreference, resultType, now
        );

        if (existing == null) {
            preInfoMapper.insertPreInfo(profile);
        } else {
            preInfoMapper.updatePreInfo(profile);
        }

        // ===== 응답 DTO 구성 =====
        return PreInfoMapperUtil.toResponseDTO(
                preInfoId, userId, token, now,
                surplus, savingsRate, score, recommended,
                investmentCapacity, resultType, riskPreference
        );
    }

    // ==== 입력양식에 따른 PREINFO_ID 명명 ====
    private String generatePreInfoId(Long userId, LocalDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return "PRE_" + userId + "_" + timestamp.format(formatter);
    }
}
