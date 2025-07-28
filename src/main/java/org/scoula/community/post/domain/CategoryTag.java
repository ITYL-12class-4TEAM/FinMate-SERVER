package org.scoula.community.post.domain;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.scoula.community.post.exception.InvalidTagException;
import org.scoula.response.ResponseCode;

@Slf4j
public enum CategoryTag {
    RECOMMEND("recommend", "추천"),
    QUESTION("question", "질문"),
    EXPERIENCE("experience", "경험"),
    FREE("free", "자유");

    private final String code;
    private final String displayName;

    CategoryTag(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }
    public static CategoryTag fromCode(String code) {
        return Arrays.stream(values())
                .filter(tag -> tag.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new InvalidTagException(ResponseCode.INVALID_CATEGORY_TAG));
    }
    public static boolean isValidCode(String code) {
        for (CategoryTag tag : CategoryTag.values()) {

            if (tag.getCode().equalsIgnoreCase(code)) {
                return true;
            }
        }
        return false;
    }
}
