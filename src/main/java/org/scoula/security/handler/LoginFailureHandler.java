package org.scoula.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.scoula.response.ResponseCode;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        String requestURI = request.getRequestURI();
        boolean isOAuth2 = requestURI.contains("/oauth2/") || requestURI.contains("/login/oauth2/code/");

        if (isOAuth2) {
            handleOAuth2Failure(response, exception);
        } else {
            handleFormLoginFailure(response, exception);
        }
    }

    private void handleOAuth2Failure(HttpServletResponse response, AuthenticationException exception) throws IOException {
        log.error("[OAuth2] 로그인 실패: {}", exception.getMessage());

        String message = exception.getMessage();
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);

        log.info("[OAuth2] 리다이렉트: message={}", message);
        response.sendRedirect("http://localhost:5173/auth/oauth2/redirect?error=oauth2_failed&message=" + encodedMessage);
    }

    private void handleFormLoginFailure(HttpServletResponse response, AuthenticationException exception) throws IOException {
        log.error("[Form Login] 로그인 실패: {}", exception.getMessage());

        ResponseCode responseCode;

        if (exception instanceof LockedException) {
            responseCode = ResponseCode.ACCOUNT_LOCKED;
        } else if (exception instanceof AccountExpiredException) {
            responseCode = ResponseCode.ACCOUNT_EXPIRED;
        } else if (exception instanceof BadCredentialsException) {
            responseCode = ResponseCode.INVALID_CREDENTIALS;
        } else {
            responseCode = ResponseCode.INVALID_CREDENTIALS;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("errorCode", responseCode.name());
        body.put("message", responseCode.getMessage());

        response.setStatus(responseCode.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), body);
    }
}