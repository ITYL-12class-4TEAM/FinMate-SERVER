package org.scoula.mypage.favorite.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class FavoriteServiceException extends BaseException {
    public FavoriteServiceException(ResponseCode responseCode) {
        super(responseCode);
    }
}