package org.scoula.preinfo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.preinfo.enums.InvestmentPeriod;
import org.scoula.preinfo.enums.PurposeCategory;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PreInformation {
    private String preInfoId;  //사전정보 고유식별자 생성규칙: PRE_{userId}_{yyyymmdd}
    private Long memberId;     //사용자 고유식별자, Principal 기반으로 추출
    private String username;    //사용자 실명 또는 표기이름

    //사용자 입력정보
    private Integer age;        //사용자의 나이
    private Long monthlyIncome; //사용자 월수입
    private Long fixedCost;     //사용자의 월 고정지출
    private InvestmentPeriod period;      // ENUM ('단기', '중기', '장기')
    private String purpose;
    private PurposeCategory purposeCategory; // ENUM('여행', '결혼', '자녀교육', '은퇴준비', '기타')

    //분석결과 (연산값)
    private Long surplusAmount; //사용자의 월 잉여자산(운용가능자산) income - fixedCost
    private Integer savingsRate;                // 소득대비저축률 (%)
    private Integer financialHealthScore;       // 재무건정성 점수 : 0~100
    private String investmentCapacity;          // 예: 부족 / 보통 / 양호
    private Long recommendedMonthlyInvestment;  // 예: 500000

    // 디바이스 정보
    private String platform;          // web / mobile
    private String userAgent;
    private String screenSize;

    private LocalDateTime createdAt;  // 사전정보 입력일
}
