package org.scoula.preinfo.exception;

import org.scoula.response.ApiResponse;
import org.scoula.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestControllerAdvice("org.scoula.preinfo")
public class PreInfoExceptionHandler {

    @ExceptionHandler(PreInfoDuplicateException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleDuplicateException(
            PreInfoDuplicateException exception,
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

    @ExceptionHandler(PreInfoAnalysisException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAnalysisException(
            PreInfoAnalysisException exception,
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

    @ExceptionHandler(PreInfoIllegalAccessException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleIllegalAccessException(
            PreInfoIllegalAccessException exception,
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


    @ExceptionHandler(PreInfoNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleResultNotFound(
            PreInfoNotFoundException exception,
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
