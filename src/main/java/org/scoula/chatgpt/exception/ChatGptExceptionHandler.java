package org.scoula.chatgpt.exception;

import java.time.LocalDateTime;
import javax.servlet.http.HttpServletRequest;
import org.scoula.response.ApiResponse;
import org.scoula.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ChatGptExceptionHandler {
    @ExceptionHandler(ChatGptDeserializationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleChatGptDeserializationException(
            ChatGptDeserializationException exception,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                exception.getMessage(),
                exception.getResponseCode().name(),
                request.getRequestURI()
        );

        ApiResponse<ErrorResponse> apiResponse = ApiResponse.fail(exception.getResponseCode(), errorResponse);

        return new ResponseEntity<>(apiResponse, exception.getResponseCode().getHttpStatus());
    }

    @ExceptionHandler(ChatGptJsonParsingException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleChatGptJsonParsingException(
            ChatGptJsonParsingException exception,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                exception.getMessage(),
                exception.getResponseCode().name(),
                request.getRequestURI()
        );

        ApiResponse<ErrorResponse> apiResponse = ApiResponse.fail(exception.getResponseCode(), errorResponse);

        return new ResponseEntity<>(apiResponse, exception.getResponseCode().getHttpStatus());
    }

    @ExceptionHandler(ChatGptRequestParsingException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleChatGptRequestParsingException(
            ChatGptRequestParsingException exception,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                exception.getMessage(),
                exception.getResponseCode().name(),
                request.getRequestURI()
        );

        ApiResponse<ErrorResponse> apiResponse = ApiResponse.fail(exception.getResponseCode(), errorResponse);

        return new ResponseEntity<>(apiResponse, exception.getResponseCode().getHttpStatus());
    }

    @ExceptionHandler(ChatGptRetrievalException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleChatGptRetrievalException(
            ChatGptRetrievalException exception,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                exception.getMessage(),
                exception.getResponseCode().name(),
                request.getRequestURI()
        );

        ApiResponse<ErrorResponse> apiResponse = ApiResponse.fail(exception.getResponseCode(), errorResponse);

        return new ResponseEntity<>(apiResponse, exception.getResponseCode().getHttpStatus());
    }
}