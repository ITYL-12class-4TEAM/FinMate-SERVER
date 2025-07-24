package org.scoula.response;

import org.springframework.http.HttpStatus;

public enum ResponseCode {
    /**
     * Member response
     */
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다,"),

    /**
     * Community - Board
     */
    BOARD_CREATE_SUCCESS(HttpStatus.OK, "게시판이 생성되었습니다"),
    BOARD_LIST_SUCCESS(HttpStatus.OK, "게시판 목록 조회 성공"),
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "게시판을 찾을 수 없습니다"),

    /**
     * Community - Post
     */
    POST_CREATE_SUCCESS(HttpStatus.OK, "게시글이 생성되었습니다"),
    POST_LIST_SUCCESS(HttpStatus.OK, "게시글 목록 조회가 성공되었습니다"),
    POST_DETAILS_SUCCESS(HttpStatus.OK, "게시글 상세 정보 조회가 성공되었습니다"),
    POST_UPDATE_SUCCESS(HttpStatus.OK, "게시글 수정 성공되었습니다"),
    POST_DELETE_SUCCESS(HttpStatus.OK, "게시글 삭제가 성공되었습니다"),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다"),

    ATTACHMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "첨부파일을 찾을 수 없습니다"),
    FILE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다"),

    /**
     * Community - Comment
     */
    COMMENT_CREATE_SUCCESS(HttpStatus.OK, "댓글이 생성되었습니다"),
    COMMENT_LIST_SUCCESS(HttpStatus.OK, "게시글의 댓글 목록 조회가 성공되었습니다"),
    COMMENT_DETAILS_SUCCESS(HttpStatus.OK, "댓글 상세 정보 조회가 성공되었습니다"),
    COMMENT_DELETE_SUCCESS(HttpStatus.OK, "댓글 삭제가 성공되었습니다"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다"),

    /**
     * Community - PostLike
     */
    POST_LIKE_CREATE_SUCCESS(HttpStatus.OK, "좋아요를 눌렀습니다."),
    POST_LIKE_CANCEL_SUCCESS(HttpStatus.OK, "좋아요를 취소했습니다."),
    POST_LIKE_COUNT_SUCCESS(HttpStatus.OK, "게시글 좋아요 개수 조회 성공"),

    /**
     * Community - CommentLike
     */
    COMMENT_LIKE_CREATE_SUCCESS(HttpStatus.OK, "좋아요를 눌렀습니다."),
    COMMENT_LIKE_CANCEL_SUCCESS(HttpStatus.OK, "좋아요를 취소했습니다."),
    COMMENT_LIKE_COUNT_SUCCESS(HttpStatus.OK, "댓글 좋아요 개수 조회 성공"),
    COMMENT_LIKE_STATUS_SUCCESS(HttpStatus.OK, "댓글 좋아요 여부 조회 성공"),

    /**
     *  ChatBot response
     */
    CHATBOT_SESSION_CREATED(HttpStatus.OK, "챗봇 세션이 생성되었습니다."),
    CHATBOT_RESPONSE_SUCCESS(HttpStatus.OK, "응답 생성 완료"),
    CHATBOT_SESSION_TERMINATED(HttpStatus.OK, "세션 종료 완료"),
    CHATBOT_SESSION_RESTORED(HttpStatus.OK, "세션이 복원되었습니다."),
    CHATBOT_HISTORY_ROLLED_BACK(HttpStatus.OK, "대화 히스토리 롤백 완료"),

    /**
     * ChatGPT response
     */
    CHATGPT_SUMMARY_SUCCESS(HttpStatus.OK, "상품 요약이 처리되었습니다."),
    CHATGPT_COMPARE_SUCCESS(HttpStatus.OK, "상품 비교가 처리되었습니다."),
    CHATGPT_REQUEST_PARSING_FAILED(HttpStatus.BAD_REQUEST, "올바른 금융 상품 내용을 요청해주세요."),
    CHATGPT_JSON_PARSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JSON 파싱에 실패했습니다."),
    CHATGPT_RETRIEVAL_FAILED(HttpStatus.UNAUTHORIZED, "금융 상품 분석 요청에 실패했습니다."),
    CHATGPT_DESERIALIZATION_FAILED(HttpStatus.BAD_REQUEST, "객체 변환에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ResponseCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
