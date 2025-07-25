package org.scoula.wmti.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum RiskPreference {
    STABILITY("안정형"),
    STABILITY_ORIENTED("안정지향형"),
    RISK_NEUTRAL("위험중립형"),
    ACTIVELY("적극투자형"),
    AGGRESSIVE("공격투자형");

    private final String label;

    RiskPreference(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

