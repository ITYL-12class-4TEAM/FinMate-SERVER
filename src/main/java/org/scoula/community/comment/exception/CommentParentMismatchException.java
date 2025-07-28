package org.scoula.community.comment.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class CommentParentMismatchException extends BaseException {
    public CommentParentMismatchException(ResponseCode responseCode) {
        super(responseCode);
    }
}
