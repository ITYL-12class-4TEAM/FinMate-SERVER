package org.scoula.preinfo.enums;

import lombok.Getter;

@Getter
public enum PurposeCategory {
    EMERGENCY("비상 자금 마련"),
    TRAVEL("여행 자금"),
    MARRIAGE("결혼 자금"),
    HOUSE_PURCHASE("주택 구입"),
    RETIREMENT("노후 준비"),
    CHILD_EDUCATION("자녀 교육비"),
    WEALTH_BUILDING("자산 증식"),
    OTHER("기타");

    private final String label;
    PurposeCategory(String label) {
        this.label = label;
    }

}
