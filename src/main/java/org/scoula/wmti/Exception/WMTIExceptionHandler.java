package org.scoula.wmti.Exception;

import org.scoula.response.ApiResponse;
import org.scoula.response.ErrorResponse;
import org.scoula.response.ResponseCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "org.scoula.wmti")
public class WMTIExceptionHandler {

    // 1. 설문 응답이 20개가 아닌 경우
    @ExceptionHandler(InvalidWMTIAnswerException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleInvalidAnswer(
            InvalidWMTIAnswerException exception,
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

    // 2. 분석/저장 중 내부 오류
    @ExceptionHandler(WMTISaveFailedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleSaveFailed(
            WMTISaveFailedException exception,
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

    // 3. 결과 조회 실패
    @ExceptionHandler(WMTIResultNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleResultNotFound(
            WMTIResultNotFoundException exception,
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

    // 4. 분석 결과 없음
    @ExceptionHandler(WMTIAnalysisNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAnalysisNotFound(
            WMTIAnalysisNotFoundException exception,
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

    // 5. 응답 형식이 JSON 배열이 아닌 경우
    @ExceptionHandler(WMTIInvalidFormatException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleInvalidFormat(
            WMTIInvalidFormatException exception,
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

    // 6. 이력 조회 실패
    @ExceptionHandler(WMTIHistoryNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleResultNotFound(
            WMTIHistoryNotFoundException exception,
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
    // 7. 설문 질문문항 불러오기 실패
    @ExceptionHandler(WMTIQuestionLoadException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleQuestionLoadFailed(
            WMTIQuestionLoadException exception,
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
