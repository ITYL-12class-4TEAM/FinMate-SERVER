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
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;


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
        String accessToken = jwtProcessor.generateAccessToken(memberId,username);
        String refreshToken = jwtProcessor.generateRefreshToken(username);
        redisService.saveAccessToken(
                memberId.toString(),
                accessToken
        );
        memberMapper.updateTokens(username, refreshToken); //
        return new AuthResultDTO(accessToken, refreshToken, memberId, UserInfoDTO.of(user.getMember()), false);

    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        log.info("[DEBUG] ===== LoginSuccessHandler 진입 =====");

        CustomUser user = (CustomUser) authentication.getPrincipal();
        boolean isOAuth2 = authentication instanceof OAuth2AuthenticationToken;

        if (isOAuth2) {
            log.info("[DEBUG] OAuth2 로그인 성공: 사용자명 = {}", user.getUsername());

            // 신규 회원 판별
            boolean isNewMember = user.getMember().getIsNewMember() != null &&
                    user.getMember().getIsNewMember();

            if (isNewMember) {
                log.info("[DEBUG] 신규 OAuth2 회원 - 추가 정보 입력 필요");

                String tempToken = jwtProcessor.generateAccessToken(
                        user.getMember().getMemberId(),
                        user.getUsername()
                );
                // 이메일과 이름 정보 추가
                String email = user.getMember().getEmail();
                String username = user.getMember().getUsername();

                // URL 인코딩
                String encodedEmail = URLEncoder.encode(email, "UTF-8");
                String encodedUsername = URLEncoder.encode(username, "UTF-8");

                // 프론트엔드로 리다이렉트 (신규 회원 - 이메일, 이름 포함)
                String redirectUrl = String.format(
                        "http://localhost:5173/auth/oauth2/redirect?token=%s&isNewMember=true&email=%s&username=%s",
                        tempToken,
                        encodedEmail,
                        encodedUsername
                );
                response.sendRedirect(redirectUrl);
                return;
            } else {
                log.info("[DEBUG] 기존 OAuth2 회원 로그인");

                AuthResultDTO result = makeAuthResult(user);

                // 프론트엔드로 리다이렉트 (기존 회원)
                String redirectUrl = String.format(
                        "http://localhost:5173/auth/oauth2/redirect?token=%s&refreshToken=%s&isNewMember=false",
                        result.getAccessToken(),
                        result.getRefreshToken()
                );
                response.sendRedirect(redirectUrl);
                return;
            }
        }

        // 일반 로그인은 JSON 응답
        AuthResultDTO result = makeAuthResult(user);
        JsonResponse.send(response, result);
    }

}
