package org.scoula.member.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class SmsException extends BaseException {
    public SmsException(ResponseCode responseCode) {
        super(responseCode);
    }
}
