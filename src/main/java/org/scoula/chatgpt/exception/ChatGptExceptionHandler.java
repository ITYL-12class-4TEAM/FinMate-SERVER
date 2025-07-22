package org.scoula.chatgpt.exception;

import java.time.LocalDateTime;
import org.scoula.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ChatGptExceptionHandler {

    @ExceptionHandler(ChatGptJsonParsingException.class)
    public ResponseEntity<ErrorResponse> handleChatGptJsonParsingException(ChatGptJsonParsingException exception, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                exception.getResponseCode().name(),
                exception.getResponseCode().getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, exception.getResponseCode().getHttpStatus());
    }

    @ExceptionHandler(ChatGptRequestParsingException.class)
    public ResponseEntity<ErrorResponse> handleChatGptRequestParsingException(ChatGptRequestParsingException exception, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                exception.getResponseCode().name(),
                exception.getResponseCode().getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, exception.getResponseCode().getHttpStatus());
    }

    @ExceptionHandler(ChatGptRetrievalException.class)
    public ResponseEntity<ErrorResponse> handleChatGptRetrievalException(ChatGptRetrievalException exception, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                exception.getResponseCode().name(),
                exception.getResponseCode().getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, exception.getResponseCode().getHttpStatus());
    }

    @ExceptionHandler(ChatGptDeserializationException.class)
    public ResponseEntity<ErrorResponse> handleChatGptDeserializationException(ChatGptDeserializationException exception, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                exception.getResponseCode().name(),
                exception.getResponseCode().getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, exception.getResponseCode().getHttpStatus());
    }
}