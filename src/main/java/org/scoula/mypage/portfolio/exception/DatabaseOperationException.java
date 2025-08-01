package org.scoula.mypage.portfolio.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class DatabaseOperationException extends BaseException {
    public DatabaseOperationException(ResponseCode responseCode) {
        super(responseCode);
    }


}
