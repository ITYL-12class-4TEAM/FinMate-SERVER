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

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static InvestmentPeriod fromValue(String value) {
        for (InvestmentPeriod p : values()) {
            if (p.label.equals(value)) return p;
        }
        throw new IllegalArgumentException("Invalid period: " + value);
    }
}
