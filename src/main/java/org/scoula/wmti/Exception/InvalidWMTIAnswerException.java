package org.scoula.wmti.Exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class InvalidWMTIAnswerException extends BaseException {
    public InvalidWMTIAnswerException(ResponseCode code) {
        super(code);
    }
}
