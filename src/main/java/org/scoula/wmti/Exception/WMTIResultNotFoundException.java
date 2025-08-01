package org.scoula.wmti.Exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class WMTIResultNotFoundException extends BaseException {
    public WMTIResultNotFoundException(ResponseCode code) {
        super(code);
    }
}
