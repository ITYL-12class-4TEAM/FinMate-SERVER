package org.scoula.mypage.recentView.exception;

import org.scoula.response.ErrorResponse;
import org.scoula.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestControllerAdvice
public class RecentViewExceptionHandler {

    @ExceptionHandler(RecentViewNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleRecentViewNotFoundException(
            RecentViewNotFoundException exception,
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

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleProductNotFoundException(
            ProductNotFoundException exception,
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