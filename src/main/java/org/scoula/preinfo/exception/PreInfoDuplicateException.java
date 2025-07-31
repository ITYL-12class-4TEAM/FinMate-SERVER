package org.scoula.preinfo.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class PreInfoDuplicateException extends BaseException {
    public PreInfoDuplicateException(ResponseCode code) {
        super(code);
    }
}
