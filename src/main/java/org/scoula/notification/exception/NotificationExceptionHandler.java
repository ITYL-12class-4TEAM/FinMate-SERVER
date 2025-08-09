package org.scoula.notification.exception;

import lombok.extern.slf4j.Slf4j;
import org.scoula.response.ApiResponse;
import org.scoula.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class NotificationExceptionHandler {

    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleNotificationException(
            NotificationException exception,
            HttpServletRequest request) {

        log.error("알림 처리 오류: {}", exception.getMessage());

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
