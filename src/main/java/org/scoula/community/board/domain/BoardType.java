package org.scoula.community.board.domain;

public enum BoardType {
    자유게시판("FREE"),
    HOT게시판("HOT"),
    공지사항("NOTICE"),
    사용자생성("CUSTOM");

    private final String code;

    BoardType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
    public static BoardType fromCode(String code) {
        for (BoardType type : BoardType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with code " + code);
    }
}
