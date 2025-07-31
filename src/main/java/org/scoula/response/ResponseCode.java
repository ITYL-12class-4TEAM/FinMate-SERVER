package org.scoula.response;

import org.scoula.auth.dto.request.PasswordCheckRequest;
import org.springframework.http.HttpStatus;

public enum ResponseCode {
    /**
     * Member response
     */
    MEMBER_INFO_SUCCESS(HttpStatus.OK, "사용자 정보 조회에 성공했습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다,"),
    MEMBER_WITHDRAW_SUCCESS(HttpStatus.OK, "회원탈퇴가 완료되었습니다."),
    MEMBER_WITHDRAW_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "회원탈퇴에 실패했습니다."),
    PROFILE_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "회원정보 수정에 실패했습니다."),
    INVALID_BIRTHDATE_FORMAT(HttpStatus.BAD_REQUEST, "생년월일 형식이 올바르지 않습니다."),
    PHONE_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "휴대폰 인증을 완료해 주세요."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "비밀번호는 8자 이상, 영문/숫자/특수문자를 포함해야 합니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    REQUIRED_TERMS_NOT_AGREED(HttpStatus.BAD_REQUEST, "필수 약관에 동의해 주세요."),
    MISSING_REQUIRED_FIELDS(HttpStatus.BAD_REQUEST, "모든 필수 입력값을 입력해 주세요."),
    SIGNUP_SUCCESS(HttpStatus.CREATED, "회원가입이 완료되었습니다."),
    VALID_EMAIL(HttpStatus.OK, "사용 가능한 이메일입니다."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    VALID_NICKNAME(HttpStatus.OK, "사용 가능한 닉네임입니다."),
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임입니다."),
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "본인인증이 필요합니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    REFRESH_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 일치하지 않습니다."),
    TOKEN_REFRESH_SUCCESS(HttpStatus.OK, "토큰이 성공적으로 갱신되었습니다."),
    ID_FIND_SUCCESS(HttpStatus.OK, "아이디 찾기에 성공했습니다."),
    PASSWORD_FIND_SUCCESS(HttpStatus.OK, "비밀번호 찾기에 성공했습니다."),
    PASSWORD_RESET_SUCCESS(HttpStatus.OK, "비밀번호가 성공적으로 변경되었습니다."),
    PROFILE_UPDATE_SUCCESS(HttpStatus.OK, "프로필이 성공적으로 수정되었습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃 성공"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근이 거부되었습니다."),
    PROFILE_IMAGE_UPLOAD_SUCCESS(HttpStatus.OK, "사진이 성공적으로 업로드되었습니다."),
    UNAUTHORIZED_USER(HttpStatus.FORBIDDEN, "로그인이 필요합니다."),
    PASSWORD_CHECK_SUCCESS(HttpStatus.OK,"본인인증에 성공했습니다.") ,   /**
     * SMS response
     */
    SMS_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "SMS 전송에 실패했습니다."),
    INVALID_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "유효하지 않은 전화번호입니다."),
    SMS_SEND_SUCCESS(HttpStatus.OK, "인증번호가 전송되었습니다."),
    SMS_VERIFY_SUCCESS(HttpStatus.OK, "인증이 완료되었습니다."),
    SMS_VERIFY_FAILED(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않습니다."),

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
    INVALID_CATEGORY_TAG(HttpStatus.BAD_REQUEST, "유효하지 않은 카테고리 태그 코드입니다"),
    INVALID_PRODUCT_TAG(HttpStatus.BAD_REQUEST, "유효하지 않은 상품 태그 코드입니다"),

    ATTACHMENT_DOWNLOAD_SUCCESS(HttpStatus.OK, "첨부파일 다운로드 성공"),
    ATTACHMENT_DELETE_SUCCESS(HttpStatus.OK, "첨부파일 삭제 성공"),

    // 스케줄러 관련
    SCHEDULER_UPDATE_SUCCESS(HttpStatus.OK, "스케줄러 업데이트 성공"),
    SCHEDULER_UPDATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "스케줄러 업데이트 실패"),

    // 핫게시물 관련
    HOT_POSTS_UPDATE_SUCCESS(HttpStatus.OK, "핫게시물 업데이트 성공"),
    HOT_POSTS_UPDATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "핫게시물 업데이트 실패"),
    HOT_POSTS_CACHE_CLEAR_SUCCESS(HttpStatus.OK, "핫게시물 캐시 삭제 성공"),
    HOT_POSTS_CACHE_CLEAR_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "핫게시물 캐시 삭제 실패"),


    /**
     * Community - Scrap
     */
    SCRAP_ADD_SUCCESS(HttpStatus.CREATED, "스크랩 추가 성공"),
    SCRAP_REMOVE_SUCCESS(HttpStatus.OK, "스크랩 해제 성공"),
    SCRAP_LIST_SUCCESS(HttpStatus.OK, "스크랩 목록 조회 성공"),
    SCRAP_COUNT_SUCCESS(HttpStatus.OK, "스크랩 수 조회 성공"),
    /**
     * Community - Comment
     */
    COMMENT_CREATE_SUCCESS(HttpStatus.OK, "댓글이 생성되었습니다"),
    COMMENT_LIST_SUCCESS(HttpStatus.OK, "게시글의 댓글 목록 조회가 성공되었습니다"),
    COMMENT_DETAILS_SUCCESS(HttpStatus.OK, "댓글 상세 정보 조회가 성공되었습니다"),
    COMMENT_DELETE_SUCCESS(HttpStatus.OK, "댓글 삭제가 성공되었습니다"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다"),
    COMMENT_PARENT_MISMATCH(HttpStatus.BAD_REQUEST, "부모 댓글이 존재하지 않거나 게시글이 일치하지 않습니다."),

    /**
     * Community - PostLike
     */
    POST_LIKE_CREATE_SUCCESS(HttpStatus.OK, "좋아요를 눌렀습니다."),
    POST_LIKE_CANCEL_SUCCESS(HttpStatus.OK, "좋아요를 취소했습니다."),
    POST_LIKE_COUNT_SUCCESS(HttpStatus.OK, "게시글 좋아요 개수 조회 성공"),
    POST_LIKE_STATUS_SUCCESS(HttpStatus.OK, "게시글 좋아요 여부 조회 성공"),
    POST_LIKE_LIST_SUCCESS(HttpStatus.OK, "내가 좋아요 누른 게시글 목록 조회 성공"),

    /**
     * Community - CommentLike
     */
    COMMENT_LIKE_CREATE_SUCCESS(HttpStatus.OK, "좋아요를 눌렀습니다."),
    COMMENT_LIKE_CANCEL_SUCCESS(HttpStatus.OK, "좋아요를 취소했습니다."),
    COMMENT_LIKE_COUNT_SUCCESS(HttpStatus.OK, "댓글 좋아요 개수 조회 성공"),
    COMMENT_LIKE_STATUS_SUCCESS(HttpStatus.OK, "댓글 좋아요 여부 조회 성공"),

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
