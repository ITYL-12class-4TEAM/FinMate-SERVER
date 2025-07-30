package org.scoula.mypage.portfolio.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class PortfolioAccessDeniedException extends BaseException {
    public PortfolioAccessDeniedException(ResponseCode responseCode) {
        super(responseCode);
    }
}
