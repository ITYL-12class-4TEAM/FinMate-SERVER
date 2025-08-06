package org.scoula.mypage.portfolio.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class PortfolioCalculationException extends BaseException {
    public PortfolioCalculationException(ResponseCode responseCode) {
        super(responseCode);
    }

}
