package org.scoula.security.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.common.service.RedisService;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.security.account.domain.CustomUser;
import org.scoula.security.account.dto.AuthResultDTO;
import org.scoula.security.account.dto.UserInfoDTO;
import org.scoula.security.util.JsonResponse;
import org.scoula.security.util.JwtProcessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;



@Log4j2
@Component
@RequiredArgsConstructor

public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtProcessor jwtProcessor;
    private final MemberMapper memberMapper;
    private final RedisService redisService;


    private AuthResultDTO makeAuthResult(CustomUser user) {
        String username = user.getUsername();
        Long memberId = user.getMember().getMemberId();
        log.info("[DEBUG] 인증 성공: 사용자명 = {}", username);
        // 토큰 생성
        String accessToken = jwtProcessor.generateAccessToken(memberId,username);
        String refreshToken = jwtProcessor.generateRefreshToken(username);
        // 토큰 + 사용자 기본 정보 (사용자명, ...)를 묶어서 AuthResultDTO 구성
        log.info("[DEBUG] accessToken: {}", accessToken);
        log.info("[DEBUG] refreshToken: {}", refreshToken);

        redisService.saveAccessToken(
                memberId.toString(),  // 키: "ACCESS:{memberId}"
                accessToken
        );
        memberMapper.updateTokens(username, refreshToken); //
        return new AuthResultDTO(accessToken, refreshToken, UserInfoDTO.of(user.getMember()));

    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 인증 결과 Principal
        CustomUser user = (CustomUser) authentication.getPrincipal();

        // 인증 성공 결과를 JSON으로 직접 응답
        AuthResultDTO result = makeAuthResult(user);
        JsonResponse.send(response, result);
    }

}
