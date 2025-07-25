package org.scoula.preinfo.service;

import lombok.RequiredArgsConstructor;
import org.scoula.preinfo.domain.PreInfoCalculator;
import org.scoula.preinfo.dto.PreInfoRequestDTO;
import org.scoula.preinfo.dto.PreInfoResponseDTO;
import org.scoula.preinfo.entity.PreInformation;
import org.scoula.preinfo.mapper.PreInfoMapper;
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
    //사전정보 등록/저장.
    @Override
    public PreInfoResponseDTO savePreInfoAndResponse(Long userId, PreInfoRequestDTO dto) {
        //현재시간 저장
        LocalDateTime now = LocalDateTime.now();

        //계산 -임시로직
        Long surplus = dto.getMonthlyIncome() - dto.getFixedCost();
        int savingsRate = (int) ((double) surplus / dto.getMonthlyIncome() * 100);
        int score = Math.min(100, savingsRate * 2); // 예시 점수 로직
        long recommendedMonthlyInvestment = (long) (surplus * 0.3); // 예시: 잉여자산의 30%

        String investmentCapacity = calculator.calculateInvestmentCapacity(savingsRate);
        RiskPreference riskPreference = calculator.calculateRiskPreference(dto, surplus, savingsRate);
        String resultType = calculator.calculateResultType(score);


        //고유 ID + 토큰생성
        String preInfoId = generatePreInfoId(userId, now);
        String token = jwtUtil.generateSurveyToken(userId); // JwtUtil 직접 구현 필요

        //엔티티 구성
        PreInformation profile = PreInformation.builder()
                .preInfoId(preInfoId)
                .memberId(userId)
                .username(dto.getUsername())
                .age(dto.getAge())
                .monthlyIncome(dto.getMonthlyIncome())
                .fixedCost(dto.getFixedCost())
                .surplusAmount(surplus)
                .period(dto.getPeriod())
                .purpose(dto.getPurpose())
                .purposeCategory(dto.getPurposeCategory())
                .savingsRate(savingsRate)
                .financialHealthScore(score)
                .investmentCapacity(investmentCapacity)
                .recommendedMonthlyInvestment(recommendedMonthlyInvestment)
                .resultType(resultType)
                .riskPreference(riskPreference)
                .platform(dto.getPlatform())
                .userAgent(dto.getUserAgent())
                .screenSize(dto.getScreenSize())
                .createdAt(now)
                .build();

        // DB 저장 -> insert/update 분기
        PreInformation existing = preInfoMapper.findByMemberId(userId);
        if (existing == null) {
            preInfoMapper.insertPreInfo(profile);
        } else {
            preInfoMapper.updatePreInfo(profile);
        }

        // DTO 로 응답 구성
        return PreInfoResponseDTO.builder()
                .preInfoId(preInfoId)
                .userId(userId)
                .savedAt(now)
                .surveyToken(token)
                .analysis(PreInfoResponseDTO.AnalysisResult.builder()
                        .disposableIncome(surplus)
                        .savingsRate(savingsRate)
                        .financialHealthScore(score)
                        .investmentCapacity(investmentCapacity)
                        .recommendedMonthlyInvestment(recommendedMonthlyInvestment)
                        .resultType(resultType)
                        .riskPreference(riskPreference)
                        .build())
                .nextStep(PreInfoResponseDTO.NextStep.builder()
                        .url("/survey/questionnaire")
                        .description("이제 투자 성향 검사를 진행해주세요.")
                        .build())
                .estimatedTime("15분") //예상소요시간 안내(고정문자열)
                .build();
    }
    //입력양식에 따른 PREINFO_ID 명명
    private String generatePreInfoId(Long userId, LocalDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return "PRE_" + userId + "_" + timestamp.format(formatter);
    }
}
