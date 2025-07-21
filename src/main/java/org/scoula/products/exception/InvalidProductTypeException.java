package org.scoula.products.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class IllegalArgumentException extends BaseException {
  public IllegalArgumentException(ResponseCode responseCode) {
    super(responseCode);
  }
}
