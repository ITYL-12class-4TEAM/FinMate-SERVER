package org.scoula.wmti.Exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class WMTIAnalysisNotFoundException extends BaseException {
    public WMTIAnalysisNotFoundException(ResponseCode code) {
        super(code);
    }
}
