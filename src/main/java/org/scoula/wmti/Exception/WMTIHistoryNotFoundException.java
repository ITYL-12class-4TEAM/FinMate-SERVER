package org.scoula.wmti.Exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class WMTIHistoryNotFoundException extends BaseException {
    public WMTIHistoryNotFoundException(ResponseCode code) {
        super(code);
    }
}
