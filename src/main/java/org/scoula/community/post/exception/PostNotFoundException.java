package org.scoula.community.post.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class PostNotFoundException extends BaseException {
    public PostNotFoundException(ResponseCode code) {
        super(code);
    }
}
