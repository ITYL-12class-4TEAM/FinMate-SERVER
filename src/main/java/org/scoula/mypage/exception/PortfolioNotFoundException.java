package org.scoula.mypage.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class PortfolioNotFoundException extends BaseException {
    public PortfolioNotFoundException() {
        super(ResponseCode.PORTFOLIO_NOT_FOUND);
    }
}