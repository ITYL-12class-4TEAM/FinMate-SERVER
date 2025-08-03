package org.scoula.security.config;

import lombok.RequiredArgsConstructor;
import org.scoula.security.filter.JwtAuthenticationFilter;
import org.scoula.security.filter.JwtUsernamePasswordAuthenticationFilter;
import org.scoula.security.handler.LoginFailureHandler;
import org.scoula.security.handler.LoginSuccessHandler;
import org.scoula.security.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService userDetailsService;
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;

    // JWT 인증 필터
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // AuthenticationManager 빈 등록
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // 필터 등록
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JwtUsernamePasswordAuthenticationFilter loginFilter =
                new JwtUsernamePasswordAuthenticationFilter(authenticationManagerBean(), loginSuccessHandler, loginFailureHandler);

        http
                .csrf().disable()
                .authorizeRequests()

                // 완전 공개 API (비회원 접근 가능)
                .antMatchers("/api/auth/**", "/api/sms/**", "/api/validation/**", "/api/signup",
                        "/api/wmti/questions", "/resources/**", "/swagger-ui.html", "/swagger-ui/**",
                        "/v2/api-docs", "/swagger-resources/**", "/webjars/**").permitAll()

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

                // 댓글 조회 (비회원 접근 가능)
                .antMatchers(HttpMethod.GET, "/api/comments/{commentId}").permitAll()           // 댓글 단건 조회
                .antMatchers(HttpMethod.GET, "/api/comments/parent/{parentCommentId}").permitAll() // 부모댓글+대댓글 조회
                .antMatchers(HttpMethod.GET, "/api/comments/post/{postId}").permitAll()         // 게시글 댓글 리스트
                .antMatchers(HttpMethod.GET, "/api/comment-like/{commentId}/count").permitAll()

                // 금융 상품 비교/요약 (비회원 접근 가능)
                .antMatchers("/api/chat/compare").permitAll()                        // 금융 상품 비교
                .antMatchers("/api/chat/summary").permitAll()                        // 금융 상품 요약
                .antMatchers("/api/wmti/analysis/all").permitAll()

                // 회원만 접근 가능한 개인화 기능
                .antMatchers("/api/post-like/**").authenticated()                    // 좋아요 기능
                .antMatchers("/api/scraps/**").authenticated()                       // 스크랩 기능
                .antMatchers("/api/posts/my").authenticated()                        // ⭐ 내가 쓴 글 (회원 전용)
                .antMatchers("/api/comments/my").authenticated()                     // ⭐ 내가 쓴 댓글 (회원 전용)
                .antMatchers(HttpMethod.POST, "/api/posts/**").authenticated()       // 게시물 작성
                .antMatchers(HttpMethod.PUT, "/api/posts/**").authenticated()        // 게시물 수정
                .antMatchers(HttpMethod.DELETE, "/api/posts/**").authenticated()     // 게시물 삭제
                .antMatchers(HttpMethod.POST, "/api/comments/**").authenticated()    // 댓글 작성
                .antMatchers(HttpMethod.DELETE, "/api/comments/**").authenticated()  // 댓글 삭제

                .anyRequest().authenticated()
                .and()
                .logout().disable()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().disable();
    }

    // 사용자 정의 인증 서비스 + 암호화 설정
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}