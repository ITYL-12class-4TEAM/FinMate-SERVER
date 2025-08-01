package org.scoula.preinfo.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class PreInfoAnalysisException extends BaseException {
  public PreInfoAnalysisException(ResponseCode code) {
    super(code);
  }
}
