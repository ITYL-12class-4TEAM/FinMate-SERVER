package org.scoula.wmti.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    //RiskPreference enum의 label이 JSON 응답에 직접 노출되도록 설정
    @JsonValue
    public String toJson() {
        return label;
    }

    @JsonCreator
    public static RiskPreference fromLabel(String input) {
        for (RiskPreference r : values()) {
            if (r.label.equals(input)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Unknown RiskPreference: " + input);
    }
}

