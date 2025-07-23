package org.scoula.community.post.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class AttachmentNotFound extends BaseException {
    public AttachmentNotFound(ResponseCode code) {
        super(code);
    }
}
