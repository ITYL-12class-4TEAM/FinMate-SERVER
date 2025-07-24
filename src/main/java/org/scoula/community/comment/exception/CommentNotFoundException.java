package org.scoula.community.comment.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class CommentNotFoundException extends BaseException {
    public CommentNotFoundException(ResponseCode code) {
        super(code);
    }
}
