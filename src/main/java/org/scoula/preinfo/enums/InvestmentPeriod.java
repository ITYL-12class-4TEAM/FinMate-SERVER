package org.scoula.preinfo.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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

    public String getLabel() {
        return label;
    }
}
