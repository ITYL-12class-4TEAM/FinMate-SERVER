package org.scoula.security.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.common.service.RedisService;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.security.util.JwtProcessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Log4j2
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final JwtProcessor jwtProcessor;
    private final UserDetailsService userDetailsService;
    private final RedisService redisService;
    private final MemberMapper memberMapper;

    private Authentication getAuthentication(String token) {
        String username = jwtProcessor.getUsername(token);
        UserDetails principal = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            String token = bearerToken.substring(BEARER_PREFIX.length());

            try {
                // 토큰 유효성 검증
                if (!jwtProcessor.validateToken(token)) {
                    log.warn("[JWT] 유효하지 않은 토큰: {}", token);
                    filterChain.doFilter(request, response);
                    return;
                }

                // 토큰의 사용자 정보 추출
                String username = jwtProcessor.getUsername(token);
                Long memberId = jwtProcessor.getMemberId(token); // 혹은 mapper를 통해 memberId 조회

                // Redis에 저장된 토큰과 일치 여부 확인
                String redisToken = redisService.get("ACCESS:" + memberId);
                if (!token.equals(redisToken)) {
                    log.warn("[JWT] Redis에 저장된 토큰과 일치하지 않음");
                    filterChain.doFilter(request, response);
                    return;
                }

                // 인증 객체 생성 및 등록
                Authentication authentication = getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                log.error("[JWT] 인증 처리 중 예외 발생: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
