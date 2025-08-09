package org.scoula.member.exception;

import org.scoula.response.ResponseCode;
import org.springframework.security.core.AuthenticationException;

public class MemberNotFoundException extends AuthenticationException {
    private final ResponseCode responseCode;

    public MemberNotFoundException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }
}