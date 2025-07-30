package org.scoula.products.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class ProductApiException extends BaseException {
    public ProductApiException(ResponseCode responseCode) {
        super(responseCode);
    }
}
