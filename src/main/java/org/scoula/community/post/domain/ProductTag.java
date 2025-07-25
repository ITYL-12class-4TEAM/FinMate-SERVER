package org.scoula.community.post.domain;

import org.scoula.community.post.exception.InvalidTagException;
import org.scoula.response.ResponseCode;

public enum ProductTag {
    DEPOSIT("deposit", "예금"),
    SAVINGS("savings", "적금"),
    FUND("fund", "펀드"),
    INSURANCE("insurance", "보험");

    private final String code;
    private final String displayName;

    ProductTag(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ProductTag fromCode(String code) {
        for (ProductTag tag : values()) {
            if (tag.code.equalsIgnoreCase(code)) {
                return tag;
            }
        }
        throw new InvalidTagException(ResponseCode.INVALID_PRODUCT_TAG);
    }
    public static boolean isValidCode(String code) {
        for (CategoryTag tag : CategoryTag.values()) {
            if (tag.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
}
