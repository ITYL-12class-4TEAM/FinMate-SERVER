package org.scoula.wmti.Exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class WMTISaveFailedException extends BaseException {
    public WMTISaveFailedException(ResponseCode code) {
        super(code);
    }
}
