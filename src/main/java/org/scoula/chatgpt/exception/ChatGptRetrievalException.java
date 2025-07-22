package org.scoula.chatgpt.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class ChatGptRetrievalException extends BaseException {
    public ChatGptRetrievalException(ResponseCode response) {
        super(response);
    }
}
