package org.scoula.notification.domain;

public enum NotificationType {
    POST_COMMENT("댓글 알림", "회원님의 게시글에 새 댓글이 달렸습니다"),
    POST_LIKE("좋아요 알림", "회원님의 게시글에 좋아요를 눌렀습니다"),
    COMMENT_REPLY("대댓글 알림", "회원님의 댓글에 답글이 달렸습니다"),
    HOT_POST("인기 게시글 알림", "관심 카테고리에 인기 게시글이 올라왔습니다"),
    SYSTEM("시스템 알림", "시스템 공지사항입니다"),
    PRODUCT_RECOMMENDATION("상품 추천 알림", "회원님께 맞는 상품을 추천드립니다");

    private final String displayName;
    private final String defaultMessage;

    NotificationType(String displayName, String defaultMessage) {
        this.displayName = displayName;
        this.defaultMessage = defaultMessage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
