package org.scoula.mypage.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class PortfolioAccessDeniedException extends BaseException {
    public PortfolioAccessDeniedException() {
        super(ResponseCode.PORTFOLIO_ACCESS_DENIED);
    }
}
