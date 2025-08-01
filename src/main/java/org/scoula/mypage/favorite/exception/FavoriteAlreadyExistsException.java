package org.scoula.mypage.favorite.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class FavoriteAlreadyExistsException extends BaseException {
    public FavoriteAlreadyExistsException(ResponseCode responseCode) {
        super(responseCode);
    }
}