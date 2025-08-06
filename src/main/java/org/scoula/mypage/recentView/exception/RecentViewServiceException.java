package org.scoula.mypage.recentView.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class RecentViewServiceException extends BaseException {
    public RecentViewServiceException(ResponseCode responseCode) {
        super(responseCode);
    }

}