package org.scoula.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
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
            log.error("[DEBUG] OAuth2 로그인 실패!");
            log.error("[DEBUG] 요청 URI: {}", requestURI);
            log.error("[DEBUG] 실패 원인: {}", exception.getMessage());
            log.error("[DEBUG] 예외 타입: {}", exception.getClass().getSimpleName());

            // 주소를 5173으로 통일
            response.sendRedirect("http://localhost:5173/login?error=oauth2_failed&message=" +
                    exception.getMessage());
            return;
        } else {
            log.error("[DEBUG] 일반 로그인 실패: {}", exception.getMessage());
        }


        String errorCode = "INVALID_CREDENTIALS";
        int status = HttpStatus.UNAUTHORIZED.value(); // 기본: 401
        String message = "사용자 ID 또는 비밀번호가 일치하지 않습니다.";

        if (exception instanceof LockedException) {
            errorCode = "ACCOUNT_LOCKED";
            status = 423;
            message = "계정이 잠겨 있습니다.";
        } else if (exception instanceof AccountExpiredException) {
            errorCode = "ACCOUNT_EXPIRED";
            status = HttpStatus.UNAUTHORIZED.value();
            message = "계정 사용 기간이 만료되었습니다.";
        } else if (exception instanceof BadCredentialsException) {
            errorCode = "INVALID_CREDENTIALS";
            status = HttpStatus.UNAUTHORIZED.value();
            message = "잘못된 인증 정보입니다.";
        }

        Map<String, Object> body = new HashMap<>();
        body.put("errorCode", errorCode);
        body.put("message", message);

        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), body);
    }
}
