package org.scoula.mypage.portfolio.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class ProductNotFoundException extends BaseException {
    public ProductNotFoundException(ResponseCode responseCode) {
        super(responseCode);
    }
}
