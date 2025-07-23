package org.scoula.chatgpt.exception;

import org.scoula.common.exception.BaseException;
import org.scoula.response.ResponseCode;

public class ChatGptJsonParsingException extends BaseException {
    public ChatGptJsonParsingException(ResponseCode response) {
        super(response);
    }
}
