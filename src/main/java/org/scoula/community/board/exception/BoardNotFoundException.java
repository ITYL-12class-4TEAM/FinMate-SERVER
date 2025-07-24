package org.scoula.community.board.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class BoardNotFoundException extends BaseException {
    public BoardNotFoundException(ResponseCode code) {
        super(code);
    }
}
