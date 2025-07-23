package org.scoula.community.post.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class UploadFailException extends BaseException {
    public UploadFailException(ResponseCode responseCode) {
        super(responseCode);
    }
}
