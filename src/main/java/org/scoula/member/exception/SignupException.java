package org.scoula.member.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class SignupException extends BaseException {
    public SignupException(ResponseCode responseCode) {
        super(responseCode);
    }
}
