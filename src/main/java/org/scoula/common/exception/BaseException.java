package org.scoula.common.exception;

import org.scoula.response.ResponseCode;

public class BaseException extends IllegalArgumentException {

    private ResponseCode responseCode;

    public BaseException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    @Override
    public String getMessage() {
        return responseCode.getMessage();
    }
}