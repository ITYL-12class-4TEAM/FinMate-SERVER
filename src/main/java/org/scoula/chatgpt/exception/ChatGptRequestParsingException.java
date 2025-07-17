package org.scoula.chatgpt.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class ChatGptRequestParsingException extends BaseException {
    public ChatGptRequestParsingException(ResponseCode response) {
        super(response);
    }
}
