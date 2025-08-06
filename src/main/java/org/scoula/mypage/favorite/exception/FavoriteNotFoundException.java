package org.scoula.mypage.favorite.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class FavoriteNotFoundException extends BaseException {
    public FavoriteNotFoundException(ResponseCode responseCode) {
        super(responseCode);
    }
}
