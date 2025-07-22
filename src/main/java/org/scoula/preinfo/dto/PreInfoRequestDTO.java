package org.scoula.preinfo.dto;

import lombok.Data;
import org.scoula.preinfo.enums.InvestmentPeriod;
import org.scoula.preinfo.enums.PurposeCategory;

@Data
public class PreInfoRequestDTO {
    private String username;                //사용자 이름
    private Integer age;                    //나이
    private Long monthlyIncome;             //월소득
    private Long fixedCost;                 //고정지출
    private InvestmentPeriod period;        //투자기간num
    private String purpose;                 // 사용 목적 상세 (직접입력 가능)
    private PurposeCategory purposeCategory;// 사용 목적 카테고리 ENUM

    // 디바이스 정보
    private String platform;
    private String userAgent;
    private String screenSize;
}
