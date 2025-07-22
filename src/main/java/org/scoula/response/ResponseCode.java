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
    CHATGPT_DESERIALIZATION_FAILED(HttpStatus.BAD_REQUEST, "객체 변환에 실패했습니다."),

    /**
     * WMTI response
     */
    WMTI_SURVEY_SUBMITTED(HttpStatus.OK, "성향 테스트가 성공적으로 제출되었습니다."),
    WMTI_CODE_GENERATED(HttpStatus.OK, "WMTI 코드가 성공적으로 생성되었습니다."),
    WMTI_SURVEY_RESULT_RETRIEVED(HttpStatus.OK, "성향 테스트 결과가 성공적으로 조회되었습니다."),
    WMTI_SURVEY_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "성향 테스트 처리 중 오류가 발생했습니다."),
    WMTI_INCOMPLETE_ANSWERS(HttpStatus.BAD_REQUEST,"20개 문항이 모두 응답되어야 합니다."),
    PREINFO_USER_PROFILE_SUBMIT_SUCCESS(HttpStatus.CREATED, "사용자 프로필이 성공적으로 제출되었습니다.");
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
