package org.scoula.preinfo.enums;

import lombok.Getter;

@Getter
public enum InvestmentPeriod {
    SHORT("단기"),
    MEDIUM("중기"),
    LONG("장기");

    private final String label;

    InvestmentPeriod(String label) {
        this.label = label;
    }
}
