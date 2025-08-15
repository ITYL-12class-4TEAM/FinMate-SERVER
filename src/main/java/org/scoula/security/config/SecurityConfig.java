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
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())  // 인증 실패 핸들러
                .accessDeniedHandler(new CustomAccessDeniedHandler())            // 인가 실패 핸들러
                .and()
                .authorizeRequests()  // authorizeHttpRequests → authorizeRequests
                .antMatchers( "/api/sms/**", "/api/validation/**", "/api/signup/**", "/oauth2/**", "/login/oauth2/code/*", "/auth/oauth2/redirect/**","/api/auth/oauth2/token",
                        "/resources/**", "/uploads/**", "/swagger-ui.html", "/swagger-ui/**", "/api/notifications/stream", "/api/auth/reset-password",
                        "/api/wmti/questions","/v2/api-docs", "/swagger-resources/**", "/webjars/**" ,"/api/auth/refresh" ,"/api/auth/find-id","/api/auth/find-password")  // 경로 수정
                .permitAll()

                // 비회원도 접근 가능한 상품 검색 및 조회 기능
                .antMatchers(HttpMethod.GET, "/api/products/**").permitAll()         // 상품 목록 조회, 필터링 항목
                .antMatchers(HttpMethod.POST, "/api/products/**").permitAll()        // 상품 검색

                // 비회원도 접근 가능한 챗봇 및 커뮤니티 기능
                .antMatchers("/api/chatbot/**").permitAll()                          // 챗봇 (비회원도 금융 질문 가능)
                .antMatchers("/api/posts/hot").permitAll()                           // 핫 게시물 조회
                .antMatchers("/api/posts/board/{boardId}").permitAll()               // 게시판별 게시물 조회
                .antMatchers("/api/posts/board/{boardId}/hot").permitAll()           // 게시판별 핫 게시물
                .antMatchers("/api/board").permitAll()                               // 게시판 목록 조회
                .antMatchers(HttpMethod.GET, "/api/posts").permitAll()               // 게시물 목록 조회 (GET만)
                .antMatchers(HttpMethod.GET, "/api/posts/{id}").permitAll()          // 개별 게시물 읽기
                .antMatchers(HttpMethod.GET, "/api/post-like/{postId}/count").permitAll()
                .antMatchers(HttpMethod.GET, "/api/scraps/posts/{postId}/count").permitAll()
                .antMatchers(HttpMethod.GET, "/api/admin/scheduler/hot-posts/update").permitAll()

                // 댓글 조회 (비회원 접근 가능)
                .antMatchers(HttpMethod.GET, "/api/comments/{commentId}").permitAll()           // 댓글 단건 조회
                .antMatchers(HttpMethod.GET, "/api/comments/parent/{parentCommentId}").permitAll() // 부모댓글+대댓글 조회
                .antMatchers(HttpMethod.GET, "/api/comments/post/{postId}").permitAll()         // 게시글 댓글 리스트
                .antMatchers(HttpMethod.GET, "/api/comment-like/{commentId}/count").permitAll()

                // 금융 상품 비교/요약 (비회원 접근 가능)
                .antMatchers("/api/chat/compare").permitAll()                        // 금융 상품 비교
                .antMatchers("/api/chat/summary").permitAll()                        // 금융 상품 요약
                .antMatchers("/api/wmti/analysis/all").permitAll()

                // 관심상품 기능 (비회원 접근 가능)
                .antMatchers(HttpMethod.GET, "/api/wishlist/populary").permitAll()   // 인기상품 조회

                // 회원만 접근 가능한 개인화 기능
                .antMatchers("/api/post-like/**").authenticated()                    // 좋아요 기능
                .antMatchers("/api/scraps/**").authenticated()                       // 스크랩 기능
                .antMatchers("/api/notifications/**").authenticated()                // 알림 기능 (추가)
                .antMatchers("/api/posts/my").authenticated()                        // ⭐ 내가 쓴 글 (회원 전용)
                .antMatchers("/api/comments/my").authenticated()                     // ⭐ 내가 쓴 댓글 (회원 전용)
                .antMatchers(HttpMethod.POST, "/api/posts/**").authenticated()       // 게시물 작성
                .antMatchers(HttpMethod.PUT, "/api/posts/**").authenticated()        // 게시물 수정
                .antMatchers(HttpMethod.DELETE, "/api/posts/**").authenticated()     // 게시물 삭제
                .antMatchers(HttpMethod.POST, "/api/comments/**").authenticated()    // 댓글 작성
                .antMatchers(HttpMethod.DELETE, "/api/comments/**").authenticated()  // 댓글 삭제
                .antMatchers("/api/wishlist/**").authenticated()                     // 관심상품 기능

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