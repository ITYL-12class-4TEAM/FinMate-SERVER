package org.scoula.notification.exception;

import org.scoula.response.ResponseCode;

public class NotificationNotFoundException extends NotificationException {
    public NotificationNotFoundException(ResponseCode responseCode) {
        super(responseCode);
    }
}
