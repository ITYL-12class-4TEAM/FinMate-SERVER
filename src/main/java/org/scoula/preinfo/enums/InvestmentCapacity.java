package org.scoula.preinfo.enums;

import lombok.Getter;

@Getter
public enum InvestmentCapacity {
    GOOD("양호"),
    NORMAL("보통"),
    INSUFFICIENT("부족"),
    RISK("위험");

    private final String label;

    InvestmentCapacity(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

