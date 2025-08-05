package org.scoula.notification.exception;

import org.scoula.response.ResponseCode;

public class NotificationException extends RuntimeException {
    private final ResponseCode responseCode;

    public NotificationException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }
}
