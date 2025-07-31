package org.scoula.preinfo.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class PreInfoNotFoundException extends BaseException {
    public PreInfoNotFoundException(ResponseCode code) {
        super(code);
    }
}

