package org.scoula.auth.controller;

import io.swagger.annotations.Api;
import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.auth.exception.TokenValidationException;
import org.scoula.auth.service.impl.LogoutServiceImpl;
import org.scoula.member.exception.MemberNotFoundException;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "로그아웃 API" , description = "액세스 토큰을 이용한 로그아웃 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class LogoutApiController {

    private final LogoutServiceImpl logoutServiceImpl;

    @PostMapping("/logout")
    @ApiOperation(value = "로그아웃", notes = "액세스 토큰을 이용한 로그아웃")

    public ApiResponse<Boolean> logout(HttpServletRequest request) {
        String accessToken = extractAccessToken(request);
        logoutServiceImpl.logout(accessToken);
        return ApiResponse.success(ResponseCode.LOGOUT_SUCCESS, true);

    }

    private String extractAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new TokenValidationException(ResponseCode.INVALID_TOKEN);
    }
}
