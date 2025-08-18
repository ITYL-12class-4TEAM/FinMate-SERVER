package org.scoula.security.config;

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
                .clientAuthenticationMethod(org.springframework.security.oauth2.core.ClientAuthenticationMethod.NONE) // 중요: NONE으로 설정
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
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        JwtUsernamePasswordAuthenticationFilter loginFilter =
                new JwtUsernamePasswordAuthenticationFilter(authenticationManager, loginSuccessHandler, loginFailureHandler);

        http
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                .authorizeRequests()

                // 1. 가장 먼저 - 공개 API들을 맨 위에 배치
                .antMatchers("/api/sms/**", "/api/validation/**", "/api/signup/**", "/oauth2/**",
                        "/login/oauth2/code/*", "/auth/oauth2/redirect/**", "/api/auth/oauth2/token",
                        "/resources/**", "/uploads/**", "/swagger-ui.html", "/swagger-ui/**",
                        "/api/notifications/stream", "/api/wmti/questions", "/v2/api-docs",
                        "/swagger-resources/**", "/webjars/**", "/api/auth/refresh",
                        "/api/auth/find-id", "/api/auth/find-password").permitAll()

                // 2. 페이징 API들을 다른 posts API보다 먼저 배치
                .antMatchers(HttpMethod.GET, "/api/posts/paging**").permitAll()              // ?page=1&size=5 포함
                .antMatchers(HttpMethod.GET, "/api/posts/board/*/paging**").permitAll()      // 게시판별 페이징

                .antMatchers(HttpMethod.GET, "/api/posts/hot**").permitAll()                 // 전체 핫게시물
                .antMatchers(HttpMethod.GET, "/api/posts/board/*/hot**").permitAll()         // 게시판별 핫게시물

                // 3. 구체적인 게시글 API들
                .antMatchers(HttpMethod.GET, "/api/posts/board/**").permitAll()              // 게시판별 게시물 조회
                .antMatchers(HttpMethod.GET, "/api/posts/**").permitAll()                    // 모든 게시물 조회 (GET만)

                // 4. 상품 관련
                .antMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/products/**").permitAll()

                // 5. 댓글 조회
                .antMatchers(HttpMethod.GET, "/api/comments/**").permitAll()

                // 6. 좋아요/스크랩 수 조회
                .antMatchers(HttpMethod.GET, "/api/post-like/*/count").permitAll()
                .antMatchers(HttpMethod.GET, "/api/comment-like/*/count").permitAll()
                .antMatchers(HttpMethod.GET, "/api/scraps/posts/*/count").permitAll()

                // 7. 기타 공개 API들
                .antMatchers("/api/chatbot/**").permitAll()
                .antMatchers("/api/chat/compare").permitAll()
                .antMatchers("/api/chat/summary").permitAll()
                .antMatchers("/api/board").permitAll()
                .antMatchers("/api/wmti/analysis/all").permitAll()
                .antMatchers(HttpMethod.GET, "/api/wishlist/populary").permitAll()
                .antMatchers(HttpMethod.GET, "/api/admin/scheduler/hot-posts/update").permitAll()

                // 8. 인증이 필요한 개인화 기능들
                .antMatchers(HttpMethod.GET, "/api/posts/my/**").authenticated()              // 내 게시글 관련 모든 API
                .antMatchers("/api/comments/my").authenticated()                             // 내가 쓴 댓글
                .antMatchers("/api/post-like/**").authenticated()                            // 좋아요 기능
                .antMatchers("/api/comment-like/**").authenticated()                         // 댓글 좋아요
                .antMatchers("/api/scraps/**").authenticated()                               // 스크랩 기능
                .antMatchers("/api/notifications/**").authenticated()                        // 알림 기능
                .antMatchers("/api/wishlist/**").authenticated()                             // 관심상품 기능

                // 9. 작성/수정/삭제 (회원 전용)
                .antMatchers(HttpMethod.POST, "/api/posts/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/posts/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/posts/**").authenticated()
                .antMatchers(HttpMethod.POST, "/api/comments/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/comments/**").authenticated()

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