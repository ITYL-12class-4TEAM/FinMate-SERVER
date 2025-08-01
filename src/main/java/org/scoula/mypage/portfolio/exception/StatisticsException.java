package org.scoula.mypage.portfolio.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class StatisticsException extends BaseException {
    public StatisticsException(ResponseCode responseCode) {
        super(responseCode);
    }

}