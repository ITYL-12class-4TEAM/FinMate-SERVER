package org.scoula.mypage.recentView.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class RecentViewNotFoundException extends BaseException {
    public RecentViewNotFoundException(ResponseCode responseCode) {
        super(responseCode);
    }
}
