package org.scoula.community.post.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class InvalidTagException extends BaseException {
    public InvalidTagException(ResponseCode responseCode) {
        super(responseCode);
    }
}
