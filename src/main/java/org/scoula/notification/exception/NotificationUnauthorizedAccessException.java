package org.scoula.notification.exception;

import org.scoula.response.ResponseCode;

public class NotificationUnauthorizedAccessException extends NotificationException {
    public NotificationUnauthorizedAccessException(ResponseCode responseCode) {
        super(responseCode);
    }
}
