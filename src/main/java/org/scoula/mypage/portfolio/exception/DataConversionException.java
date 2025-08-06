package org.scoula.mypage.portfolio.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class DataConversionException extends BaseException {
    public DataConversionException(ResponseCode responseCode) {
        super(responseCode);
    }

}