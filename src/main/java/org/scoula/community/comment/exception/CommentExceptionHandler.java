package org.scoula.community.comment.exception;


import java.time.LocalDateTime;
import javax.servlet.http.HttpServletRequest;
import org.scoula.response.ApiResponse;
import org.scoula.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommentExceptionHandler {

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleCommentNotFoundException(
            CommentNotFoundException exception,
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
    @ExceptionHandler(CommentParentMismatchException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleCommentParentMismatchException(
            CommentParentMismatchException exception,
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