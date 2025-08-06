package org.scoula.mypage.favorite.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class ValidationException extends BaseException {
    public ValidationException(ResponseCode responseCode) {
        super(responseCode);
    }
}