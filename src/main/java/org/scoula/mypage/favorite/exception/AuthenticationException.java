package org.scoula.mypage.favorite.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class AuthenticationException extends BaseException {
    public AuthenticationException(ResponseCode responseCode) {
        super(responseCode);
    }
}