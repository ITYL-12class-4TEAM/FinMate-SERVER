package org.scoula.preinfo.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum PurposeCategory {
    EMERGENCY("비상자금 마련"),
    TRAVEL("여행자금"),
    MARRIAGE("결혼자금"),
    HOUSE_PURCHASE("주택구입"),
    RETIREMENT("노후준비"),
    CHILD_EDUCATION("자녀교육비"),
    WEALTH_BUILDING("자산증식"),
    OTHER("기타");

    private final String label;
    PurposeCategory(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static PurposeCategory fromLabel(String value) {
        for (PurposeCategory c : values()) {
            if (c.label.equals(value)) return c;
        }
        throw new IllegalArgumentException("Invalid purposeCategory: " + value);
    }
}
