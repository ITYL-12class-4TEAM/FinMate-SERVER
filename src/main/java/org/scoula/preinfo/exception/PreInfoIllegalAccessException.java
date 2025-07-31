package org.scoula.preinfo.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class PreInfoIllegalAccessException extends BaseException {
  public PreInfoIllegalAccessException(ResponseCode code) {
    super(code);
  }
}
