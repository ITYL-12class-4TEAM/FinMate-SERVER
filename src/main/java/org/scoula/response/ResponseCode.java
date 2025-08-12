package org.scoula.response;

import org.springframework.http.HttpStatus;

public enum ResponseCode {
    /**
     * Auth / Token 관련 응답
     */
    AUTH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "인증 토큰이 존재하지 않거나 올바르지 않습니다."),
    AUTH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "인증 토큰이 만료되었습니다."),
    AUTH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 인증 토큰입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),

    /**
     * Member response
     */
    LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "사용자 ID 또는 비밀번호가 일치하지 않습니다."),
    MEMBER_DELETED(HttpStatus.FORBIDDEN, "탈퇴된 회원입니다."),
    ACCOUNT_LOCKED(HttpStatus.LOCKED, "계정이 잠겨 있습니다."),
    ACCOUNT_EXPIRED(HttpStatus.UNAUTHORIZED, "계정 사용 기간이 만료되었습니다."),
    MEMBER_INFO_SUCCESS(HttpStatus.OK, "사용자 정보 조회에 성공했습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다,"),
    MEMBER_WITHDRAW_SUCCESS(HttpStatus.OK, "회원탈퇴가 완료되었습니다."),
    MEMBER_WITHDRAW_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "회원탈퇴에 실패했습니다."),
    AUTH_ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    PROFILE_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "회원정보 수정에 실패했습니다."),
    INVALID_BIRTHDATE_FORMAT(HttpStatus.BAD_REQUEST, "생년월일 형식이 올바르지 않습니다."),
    PHONE_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "휴대폰 인증을 완료해 주세요."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "비밀번호는 8자 이상, 영문/숫자/특수문자를 포함해야 합니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    EMAIL_MISMATCH(HttpStatus.BAD_REQUEST, "이메일이 일치하지 않습니다."),
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
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근이 거부되었습니다."),
    PROFILE_IMAGE_UPLOAD_SUCCESS(HttpStatus.OK, "사진이 성공적으로 업로드되었습니다."),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    PASSWORD_CHECK_SUCCESS(HttpStatus.OK,"본인인증에 성공했습니다.") ,
    TOKEN_EXCHANGE_FAILED(HttpStatus.UNAUTHORIZED, "토큰 교환에 실패했습니다."),

    /**
     * OAuth2 / Social Login 관련 응답
     */

    OAUTH2_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "소셜 로그인에 실패했습니다."),
    OAUTH2_EMAIL_NOT_PROVIDED(HttpStatus.BAD_REQUEST, "소셜 계정에서 이메일 정보를 가져올 수 없습니다."),
    OAUTH2_DIFFERENT_SOCIAL_TYPE(HttpStatus.CONFLICT, "다른 소셜 계정으로 이미 가입된 이메일입니다."),
    SOCIAL_SIGNUP_SUCCESS(HttpStatus.CREATED, "소셜 회원가입이 완료되었습니다."),
    EMAIL_ALREADY_REGISTERED(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    INVALID_AGE_RANGE(HttpStatus.BAD_REQUEST, "유효하지 않은 나이 범위입니다"),
    INVALID_ASSET_RANGE(HttpStatus.BAD_REQUEST, "유효하지 않은 자산 범위입니다"),
    INVALID_WMTI_CODE_FORMAT(HttpStatus.BAD_REQUEST, "WMTI 코드 형식이 올바르지 않습니다"),
    INVALID_CATEGORY_OR_SUBCATEGORY(HttpStatus.BAD_REQUEST, "카테고리 및 소분류는 필수입니다."),

    /**
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

    // 상품 수집 관련
    PRODUCT_COMPANY_FETCH_SUCCESS(HttpStatus.OK,"상품사 수집 성공"),
    DEPOSIT_PRODUCT_FETCH_SUCCESS(HttpStatus.OK,"예금상품 수집 성공"),
    PENSION_PRODUCT_FETCH_SUCCESS(HttpStatus.OK,"연금상품 수집 성공"),
    SAVING_PRODUCT_FETCH_SUCCESS(HttpStatus.OK,"적금상품 수집 성공"),

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
    CHATGPT_PRODUCT_INFO_SUCCESS(HttpStatus.OK, "상품 정보 요청이 성공적으로 처리되었습니다."),
    CHATGPT_PRODUCT_INFO_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "상품 정보 요청에 실패했습니다."),
    CHATGPT_ETC_CATEGORY_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "기타 카테고리는 지원하지 않습니다."),
    /**
     * Portfolio response
     */
    PORTFOLIO_CREATE_SUCCESS(HttpStatus.CREATED, "포트폴리오 생성 성공"),
    PORTFOLIO_READ_SUCCESS(HttpStatus.OK, "포트폴리오 목록 조회 성공"),
    PORTFOLIO_UPDATE_SUCCESS(HttpStatus.OK, "포트폴리오 수정 성공"),
    PORTFOLIO_DELETE_SUCCESS(HttpStatus.OK, "포트폴리오 삭제 성공"),
    PORTFOLIO_SUMMARY_SUCCESS(HttpStatus.OK, "포트폴리오 요약 조회 성공"),
    PORTFOLIO_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "포트폴리오 목록 조회에 실패했습니다"),
    PORTFOLIO_SUMMARY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "포트폴리오 요약 조회에 실패했습니다"),
    PORTFOLIO_SUMMARY_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "포트폴리오 요약 데이터 처리에 실패했습니다"),
    PORTFOLIO_CALCULATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "포트폴리오 계산에 실패했습니다"),
    PORTFOLIO_NOT_FOUND(HttpStatus.NOT_FOUND, "포트폴리오를 찾을 수 없습니다"),
    PORTFOLIO_ACCESS_DENIED(HttpStatus.FORBIDDEN, "포트폴리오 접근 권한이 없습니다"),
    INVALID_AGE_INFO(HttpStatus.BAD_REQUEST, "유효하지 않은 나이 정보입니다"),
    INVALID_ASSET_INFO(HttpStatus.BAD_REQUEST, "유효하지 않은 자산 정보입니다"),
    INVALID_WMTI_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 WMTI 코드입니다"),
    INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST, "날짜 형식이 올바르지 않습니다"),
    CALCULATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "계산 처리 중 오류가 발생했습니다"),
    COMPARISON_STATS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "비교 통계 조회에 실패했습니다"),
    AGE_GROUP_COMPARISON_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "연령대별 비교 데이터 조회에 실패했습니다"),
    AMOUNT_GROUP_COMPARISON_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "자산 규모별 비교 데이터 조회에 실패했습니다"),
    WMTI_COMPARISON_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "WMTI별 비교 데이터 조회에 실패했습니다"),
    AGE_GROUP_STATS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "나이대별 통계 조회에 실패했습니다"),
    INVALID_STAT_FORMAT(HttpStatus.BAD_REQUEST,"통계 데이터 형식이 올바르지 않습니다."),
    PENSION_RATE_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "연금률 처리에 실패했습니다"),
    DATA_CONVERSION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "데이터 변환에 실패했습니다"),
    DTO_CONVERSION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "DTO 변환에 실패했습니다"),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다"),

    /**
     * favorite response
     */
    FAVORITE_CREATE_SUCCESS(HttpStatus.CREATED, "관심상품 등록 성공"),
    FAVORITE_DELETE_SUCCESS(HttpStatus.OK, "관심상품 삭제 성공"),
    FAVORITE_READ_SUCCESS(HttpStatus.OK, "관심상품 목록 조회 성공"),
    FAVORITE_STATUS_CHECK_SUCCESS(HttpStatus.OK, "관심상품 여부 확인 성공"),
    POPULAR_FAVORITE_READ_SUCCESS(HttpStatus.OK, "인기 관심상품 조회 성공"),

    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "관심상품을 찾을 수 없습니다"),
    FAVORITE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 관심상품으로 등록된 상품입니다"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다"),

    FAVORITE_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "관심상품 목록 조회에 실패했습니다"),
    FAVORITE_CHECK_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "관심상품 여부 확인에 실패했습니다"),
    POPULAR_FAVORITE_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "인기 관심상품 조회에 실패했습니다"),

    /**
     * RecentView response
     */
    RECENT_VIEW_CREATE_SUCCESS(HttpStatus.CREATED, "최근 본 상품 저장 성공"),
    RECENT_VIEW_READ_SUCCESS(HttpStatus.OK, "최근 본 상품 목록 조회 성공"),
    RECENT_VIEW_DELETE_SUCCESS(HttpStatus.OK, "최근 본 상품 삭제 성공"),
    RECENT_VIEW_DELETE_ALL_SUCCESS(HttpStatus.OK, "최근 본 상품 전체 삭제 성공"),

    RECENT_VIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "최근 본 상품 기록을 찾을 수 없습니다"),
    RECENT_VIEW_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "최근 본 상품 목록 조회에 실패했습니다"),
    RECENT_VIEW_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "최근 본 상품 저장에 실패했습니다"),
    RECENT_VIEW_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "최근 본 상품 삭제에 실패했습니다"),

    /**
     *  database operation response
     */
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다"),

    /**
     * 유효성 검증 관련
     */
    INVALID_DAYS_RANGE_MIN(HttpStatus.BAD_REQUEST, "조회 기간은 1일 이상이어야 합니다"),
    INVALID_DAYS_RANGE_MAX(HttpStatus.BAD_REQUEST, "조회 기간은 365일을 초과할 수 없습니다"),
    INVALID_PRODUCT_ID(HttpStatus.BAD_REQUEST, "상품 ID는 필수입니다"),
    INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "가입 금액은 0보다 커야 합니다"),
    INVALID_SAVE_TERM(HttpStatus.BAD_REQUEST, "저축 기간은 0보다 커야 합니다"),
    INVALID_MEMBER_ID(HttpStatus.BAD_REQUEST, "회원 ID는 양수여야 합니다"),


    /**
     * Product response
     */
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
    SUBCATEGORY_SUCCESS(HttpStatus.OK, "서브 카테고리 조회에 성공했습니다."),
    /**
     * PreInfo response
     */
    PREINFO_USER_PROFILE_SUBMIT_SUCCESS(HttpStatus.CREATED, "사용자 프로필이 성공적으로 제출되었습니다."),
    PREINFO_NOT_FOUND(HttpStatus.NOT_FOUND, "사전 정보가 존재하지 않습니다."),
    PREINFO_RETRIEVED(HttpStatus.OK, "사전 정보가 성공적으로 조회되었습니다."),
    PREINFO_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 동일한 사전 정보가 존재합니다."),
    PREINFO_ANALYSIS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "사전 정보 분석 중 오류가 발생했습니다."),
    PREINFO_ILLEGAL_REQUEST(HttpStatus.BAD_REQUEST, "올바르지 않은 요청입니다."),
    /**
     * Notification response
     */
    NOTIFICATION_LIST_SUCCESS(HttpStatus.OK, "알림 목록 조회에 성공했습니다."),
    NOTIFICATION_READ_SUCCESS(HttpStatus.OK, "알림 읽음 처리에 성공했습니다."),
    NOTIFICATION_READ_ALL_SUCCESS(HttpStatus.OK, "모든 알림 읽음 처리에 성공했습니다."),
    NOTIFICATION_CREATE_SUCCESS(HttpStatus.CREATED, "알림이 생성되었습니다."),
    NOTIFICATION_SETTINGS_UPDATE_SUCCESS(HttpStatus.OK, "알림 설정이 업데이트되었습니다."),
    NOTIFICATION_SETTINGS_GET_SUCCESS(HttpStatus.OK, "알림 설정 조회에 성공했습니다."),
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다."),
    NOTIFICATION_UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "해당 알림에 접근할 권한이 없습니다."),
    NOTIFICATION_ALREADY_READ(HttpStatus.BAD_REQUEST, "이미 읽은 알림입니다."),
    NOTIFICATION_INVALID_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 알림 타입입니다."),
    NOTIFICATION_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "알림 생성에 실패했습니다."),

    /**
     * WMTI response
     */
    WMTI_SURVEY_SUBMITTED(HttpStatus.OK, "성향 테스트가 성공적으로 제출되었습니다."),
    WMTI_CODE_GENERATED(HttpStatus.OK, "WMTI 코드가 성공적으로 생성되었습니다."),
    WMTI_SURVEY_RESULT_RETRIEVED(HttpStatus.OK, "성향 테스트 결과가 성공적으로 조회되었습니다."),
    WMTI_RESULT_NOT_FOUND(HttpStatus.NOT_FOUND, "설문 결과가 존재하지 않습니다."),
    WMTI_RESULT_RETRIEVAL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "설문 결과 조회 중 오류가 발생했습니다."),
    WMTI_SURVEY_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "성향 테스트 처리 중 오류가 발생했습니다."),
    WMTI_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "설문 결과 저장에 실패했습니다."),
    WMTI_INVALID_ANSWER_FORMAT(HttpStatus.BAD_REQUEST, "응답 형식이 올바르지 않습니다."),
    WMTI_INCOMPLETE_ANSWERS(HttpStatus.BAD_REQUEST,"20개 문항이 모두 응답되어야 합니다."),
    // WMTI설문 이력 관련
    WMTI_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "설문 이력이 존재하지 않습니다."),
    WMTI_HISTORY_RETRIEVED(HttpStatus.OK, "설문 이력이 조회되었습니다."),
    WMTI_HISTORY_RETRIEVAL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "설문 이력 조회 중 오류가 발생했습니다."),
    // 분석 결과 관련
    WMTI_ANALYSIS_SUCCESS(HttpStatus.OK, "분석 결과가 조회되었습니다."),
    WMTI_ANALYSIS_NOT_FOUND(HttpStatus.NOT_FOUND, "분석 결과를 찾을 수 없습니다."),
    WMTI_ANALYSIS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "분석 결과 처리 중 오류가 발생했습니다."),
    //공개/비공개 관련
    WMTI_RESULT_PRIVATE(HttpStatus.FORBIDDEN, "비공개 상태의 결과입니다."),
        //설문 질문문항 관련
        WMTI_QUESTION_RETRIEVED(HttpStatus.OK, "WMTI 설문 문항 조회 성공"),
        WMTI_QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "WMTI 설문 문항 파일을 찾을 수 없음"),
        WMTI_QUESTION_LOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "WMTI 설문 문항 파싱 실패");
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
