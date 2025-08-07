package org.scoula.security.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.common.config.AppProperties;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;


@Log4j2
@Component
@RequiredArgsConstructor

public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtProcessor jwtProcessor;
    private final MemberMapper memberMapper;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private final AppProperties appProperties;


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
        return new AuthResultDTO(accessToken, refreshToken, UserInfoDTO.of(user.getMember()), false);

    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {


        CustomUser user = (CustomUser) authentication.getPrincipal();
        boolean isOAuth2 = authentication instanceof OAuth2AuthenticationToken;

        if (isOAuth2) {
            log.info("[DEBUG] OAuth2 로그인 성공: 사용자명 = {}", user.getUsername());

            // 신규 회원 판별
            boolean isNewMember = user.getMember().getIsNewMember() != null &&
                    user.getMember().getIsNewMember();

            if (isNewMember) {

                String tempToken = jwtProcessor.generateAccessToken(
                        user.getMember().getMemberId(),
                        user.getUsername()
                );


                String email = user.getMember().getEmail();
                String username = user.getMember().getUsername();

                String encodedEmail = URLEncoder.encode(email, "UTF-8");
                String encodedUsername = URLEncoder.encode(username, "UTF-8");
                String redirectUrl = String.format(
                        "%s?isNewMember=true&email=%s&username=%s",
                        appProperties.getFrontendOAuth2RedirectUrl(),
                        encodedEmail,
                        encodedUsername
                );
                response.sendRedirect(redirectUrl);
                return;
            } else {
                log.info("[DEBUG] 기존 OAuth2 회원 로그인");

                AuthResultDTO result = makeAuthResult(user);

                String tempCode = UUID.randomUUID().toString();
                String resultJson = objectMapper.writeValueAsString(result);
                redisService.save("code:" + tempCode, resultJson, 300);

                String redirectUrl = String.format(
                        "%s?code=%s&isNewMember=false",
                        appProperties.getFrontendOAuth2RedirectUrl(),
                        tempCode
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
