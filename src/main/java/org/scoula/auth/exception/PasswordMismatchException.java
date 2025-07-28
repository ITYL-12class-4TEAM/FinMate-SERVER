package org.scoula.auth.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class PasswordMismatchException extends BaseException {
    public PasswordMismatchException(ResponseCode responseCode) {
        super(responseCode);
    }
}
