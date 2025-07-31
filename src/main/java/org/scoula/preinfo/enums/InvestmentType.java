package org.scoula.preinfo.enums;

import lombok.Getter;

@Getter
public enum InvestmentType {
    AGGRESSIVE("고수익 지향형"),
    ACTIVE("적극적 설계형"),
    BALANCED("균형잡힌 실속형"),
    PASSIVE("소극적 관리형");

    private final String label;

    InvestmentType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
