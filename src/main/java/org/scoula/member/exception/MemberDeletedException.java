package org.scoula.member.exception;

import org.scoula.response.ResponseCode;
import org.springframework.security.core.AuthenticationException;

public class MemberDeletedException extends AuthenticationException {
    private final ResponseCode responseCode;

    public MemberDeletedException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }
}