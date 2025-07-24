package org.scoula.mypage.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class InvalidPortfolioInputException extends BaseException {
    public InvalidPortfolioInputException() {
        super(ResponseCode.INVALID_INPUT_FORMAT);
    }
}
