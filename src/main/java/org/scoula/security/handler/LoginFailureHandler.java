package org.scoula.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.response.ResponseCode;
import org.scoula.common.config.AppProperties;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
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
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final AppProperties appProperties;

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


        String message = exception.getMessage();
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);

        response.sendRedirect(appProperties.getFrontendOAuth2RedirectUrl() +
                "?error=oauth2_failed&message=" + encodedMessage);
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