package org.scoula.security.config;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.scoula.auth.oauth2.OAuth2Properties;
import org.scoula.security.filter.JwtAuthenticationFilter;
import org.scoula.security.filter.JwtUsernamePasswordAuthenticationFilter;
import org.scoula.security.handler.CustomAccessDeniedHandler;
import org.scoula.security.handler.CustomAuthenticationEntryPoint;
import org.scoula.security.handler.LoginFailureHandler;
import org.scoula.security.handler.LoginSuccessHandler;
import org.scoula.auth.oauth2.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2Properties oauth2Properties;
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(
                googleClientRegistration(),
                kakaoClientRegistration()
        );
    }

    @Bean
    public ClientRegistration googleClientRegistration() {
        System.out.println("url=== " + oauth2Properties.getGoogle().getRedirectUri());
        return ClientRegistration.withRegistrationId("google")
                .clientId(oauth2Properties.getGoogle().getClientId())
                .clientSecret(oauth2Properties.getGoogle().getClientSecret())
                .scope("profile", "email")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("sub")
                .clientName("Google")
                .redirectUri(oauth2Properties.getGoogle().getRedirectUri())
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();
    }

    @Bean
    public ClientRegistration kakaoClientRegistration() {
        return ClientRegistration.withRegistrationId("kakao")
                .clientId(oauth2Properties.getKakao().getClientId())
                .scope("profile_nickname", "account_email")
                .authorizationUri("https://kauth.kakao.com/oauth/authorize")
                .tokenUri("https://kauth.kakao.com/oauth/token")
                .userInfoUri("https://kapi.kakao.com/v2/user/me")
                .userNameAttributeName("id")
                .clientName("Kakao")
                .redirectUri(oauth2Properties.getKakao().getRedirectUri())
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientAuthenticationMethod(org.springframework.security.oauth2.core.ClientAuthenticationMethod.NONE)
                .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(
                "/v2/api-docs",
                "/swagger-resources/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/webjars/**"
        );
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        JwtUsernamePasswordAuthenticationFilter loginFilter =
                new JwtUsernamePasswordAuthenticationFilter(authenticationManager, loginSuccessHandler, loginFailureHandler);

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 추가
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                .authorizeRequests()

                // 1. 인증/권한 관련 공개 API
                .antMatchers("/api/sms/**", "/api/validation/**", "/api/signup/**", 
                           "/oauth2/**", "/login/oauth2/code/*", "/auth/oauth2/redirect/**", 
                           "/api/auth/oauth2/token", "/api/auth/refresh", "/api/auth/find-id", 
                           "/api/auth/find-password", "/api/auth/reset-password").permitAll()

                // 2. 정적 리소스 및 문서
                .antMatchers("/resources/**", "/uploads/**", "/swagger-ui.html", 
                           "/swagger-ui/**", "/v2/api-docs", "/swagger-resources/**", 
                           "/webjars/**").permitAll()

                // 3. 페이징 API (더 구체적인 패턴을 먼저 배치)
                .antMatchers(HttpMethod.GET, "/api/posts/paging**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/posts/board/*/paging**").permitAll()

                // 4. 핫 게시물 API
                .antMatchers(HttpMethod.GET, "/api/posts/hot**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/posts/board/*/hot**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/hot-posts/clear-cache").permitAll()
                .antMatchers(HttpMethod.POST, "/api/admin/scheduler/hot-posts/clear-cache").permitAll()
                .antMatchers(HttpMethod.POST, "/api/admin/scheduler/hot-posts/update").permitAll()

                // 5. 게시판 및 게시물 조회 (비회원 접근 가능)
                .antMatchers("/api/board").permitAll()
                .antMatchers(HttpMethod.GET, "/api/posts").permitAll()
                .antMatchers(HttpMethod.GET, "/api/posts/{id}").permitAll()
                .antMatchers(HttpMethod.GET, "/api/posts/board/**").permitAll()

                // 6. 댓글 조회 (비회원 접근 가능)
                .antMatchers(HttpMethod.GET, "/api/comments/**").permitAll()

                // 7. 좋아요/스크랩 수 조회 (비회원 접근 가능)
                .antMatchers(HttpMethod.GET, "/api/post-like/*/count").permitAll()
                .antMatchers(HttpMethod.GET, "/api/comment-like/*/count").permitAll()
                .antMatchers(HttpMethod.GET, "/api/scraps/posts/*/count").permitAll()

                // 8. 상품 관련 (비회원 접근 가능)
                .antMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/products/**").permitAll()

                // 9. 챗봇 및 금융 상품 비교/요약 (비회원 접근 가능)
                .antMatchers("/api/chatbot/**").permitAll()
                .antMatchers("/api/chat/compare").permitAll()
                .antMatchers("/api/chat/summary").permitAll()

                // 10. 기타 공개 API
                .antMatchers("/api/notifications/stream").permitAll()
                .antMatchers("/api/wmti/questions").permitAll()
                .antMatchers("/api/wmti/analysis/all").permitAll()
                .antMatchers(HttpMethod.GET, "/api/wishlist/populary").permitAll()
                .antMatchers(HttpMethod.GET, "/api/admin/scheduler/hot-posts/update").permitAll()

                // 11. 인증이 필요한 개인화 기능들
                .antMatchers(HttpMethod.GET, "/api/posts/my/**").authenticated()
                .antMatchers("/api/comments/my").authenticated()
                .antMatchers("/api/post-like/**").authenticated()
                .antMatchers("/api/comment-like/**").authenticated()
                .antMatchers("/api/scraps/**").authenticated()
                .antMatchers("/api/notifications/**").authenticated()
                .antMatchers("/api/wishlist/**").authenticated()

                // 12. 작성/수정/삭제 (회원 전용)
                .antMatchers(HttpMethod.POST, "/api/posts/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/posts/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/posts/**").authenticated()
                .antMatchers(HttpMethod.POST, "/api/comments/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/comments/**").authenticated()

                // 13. 나머지 모든 요청은 인증 필요
                .anyRequest().authenticated()
                .and()
                .logout().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorization")
                .and()
                .redirectionEndpoint()
                .baseUri("/login/oauth2/code/*")
                .and()
                .userInfoEndpoint()
                .userService(customOAuth2UserService)
                .and()
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler)
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}