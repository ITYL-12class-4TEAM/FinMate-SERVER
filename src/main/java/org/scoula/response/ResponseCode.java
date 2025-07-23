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
     * ChatBot response
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
    CHATGPT_DESERIALIZATION_FAILED(HttpStatus.BAD_REQUEST, "객체 변환에 실패했습니다."),

    /**
     * Product response
     */
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    PRODUCT_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "금융상품 API 호출 중 오류가 발생했습니다."),
    PRODUCT_SEARCH_SUCCESS(HttpStatus.OK, "상품 검색이 성공적으로 처리되었습니다."),
    PRODUCT_DETAIL_SUCCESS(HttpStatus.OK, "상품 상세 정보를 성공적으로 조회했습니다."),
    PRODUCT_COMPARE_SUCCESS(HttpStatus.OK, "상품 비교가 성공적으로 처리되었습니다."),
    INVALID_PRODUCT_TYPE_ERROR(HttpStatus.BAD_REQUEST, "유효하지 않는 상품 유형입니다"),
    PRODUCT_INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 상품 파라미터가 입력되었습니다."),
    PRODUCT_AUTOCOMPLETE_SUCCESS(HttpStatus.OK, "상품 검색 자동완성을 성공했습니다."),
    PRODUCT_CATEGORY_SUCCESS(HttpStatus.OK, "상품 카테고리 목록 조회를 성공했습니다."),
    PRODUCT_FILTER_OPTIONS_SUCCESS(HttpStatus.OK, "상품 필터링 기능이 성공적으로 처리되었습니다."),
    PRODUCT_COMPARISON_SUCCESS(HttpStatus.OK, "상품 비교를 성공했습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리 조회에 실패했습니다."),
    SUBCATEGORY_SUCCESS(HttpStatus.OK, "서브 카테고리 조회에 성공했습니다.");


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
