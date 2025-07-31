package org.scoula.wmti.Exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class WMTIInvalidFormatException extends BaseException {
    public WMTIInvalidFormatException(ResponseCode code) {
        super(code);
    }
}
