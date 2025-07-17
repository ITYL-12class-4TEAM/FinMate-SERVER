package org.scoula.response;

import org.springframework.http.HttpStatus;

public enum ResponseCode {
    //Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다,");

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
