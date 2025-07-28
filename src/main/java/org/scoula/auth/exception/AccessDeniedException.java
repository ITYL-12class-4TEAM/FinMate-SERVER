package org.scoula.auth.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class AccessDeniedException extends BaseException {
    public AccessDeniedException(ResponseCode responseCode) {
        super(responseCode);
    }
}
