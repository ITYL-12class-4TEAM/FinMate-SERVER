package org.scoula.member.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class MemberNotFoundException extends BaseException {
    public MemberNotFoundException(ResponseCode responseCode) {
        super(responseCode);
    }
}
