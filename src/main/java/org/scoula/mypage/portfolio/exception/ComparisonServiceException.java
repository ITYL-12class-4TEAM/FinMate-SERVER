package org.scoula.mypage.portfolio.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class ComparisonServiceException extends BaseException {
    public ComparisonServiceException(ResponseCode responseCode) {
        super(responseCode);
    }


}
