package org.scoula.community.post.domain;

import lombok.extern.log4j.Log4j2;
import org.scoula.community.post.exception.InvalidTagException;
import org.scoula.response.ResponseCode;

@Log4j2
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
        for (ProductTag tag : ProductTag.values()) {
            log.info("tag : " + tag);
            log.info("code : " + code);
            if (tag.getCode().equalsIgnoreCase(code)) { // 대소문자 구분 없이 비교
                return true;
            }
        }
        System.out.println("2false");
        return false;
    }
}
