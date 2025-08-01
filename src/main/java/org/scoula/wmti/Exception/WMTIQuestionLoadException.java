package org.scoula.wmti.Exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class WMTIQuestionLoadException extends BaseException {
    public WMTIQuestionLoadException(ResponseCode code) {
        super(code);
    }
}
