package org.scoula.community.post.domain;

public enum PostStatus {
    NORMAL("NORMAL", "정상"),
    HIDDEN("HIDDEN", "블라인드"),
    DELETED("DELETED", "삭제됨"),
    REPORTED("REPORTED", "신고로 숨김"),
    BLOCKED("BLOCKED", "관리자에 의해 차단됨"),
    DRAFT("DRAFT", "임시 저장"),
    ARCHIVED("ARCHIVED", "보관됨");

    private final String code;
    private final String label;

    PostStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static PostStatus fromCode(String code) {
        for (PostStatus status : PostStatus.values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown PostStatus code: " + code);
    }
}