package org.scoula.chatgpt.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class ChatGptDeserializationException extends BaseException {
    public ChatGptDeserializationException(ResponseCode responseCode) {
        super(responseCode);
    }
}
