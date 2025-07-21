package org.scoula.products.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class InvalidProductTypeException extends BaseException {
  public InvalidProductTypeException(ResponseCode responseCode) {
    super(responseCode);
  }
}
