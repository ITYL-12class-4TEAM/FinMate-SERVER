package org.scoula.mypage.portfolio.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class PortfolioNotFoundException extends BaseException {
    public PortfolioNotFoundException(ResponseCode responseCode) {
        super(responseCode);
    }
}