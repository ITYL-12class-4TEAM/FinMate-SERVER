package org.scoula.auth.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class InvalidPasswordFormatException extends BaseException {
    public InvalidPasswordFormatException(ResponseCode responseCode) {
        super(responseCode);
    }
}
