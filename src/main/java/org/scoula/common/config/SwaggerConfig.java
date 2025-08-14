package org.scoula.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Set;

@Configuration        // Spring 설정 클래스임을 명시
@EnableSwagger2      // Swagger 2.0 활성화
public class SwaggerConfig {

    // API 문서 메타 정보 상수
    private final String API_NAME = "FinMate API";
    private final String API_VERSION = "1.0";
    private final String API_DESCRIPTION = "FinMate API 명세서";

    // 환경별 호스트 설정 (application.properties에서 주입)
    @Value("${swagger.host:54.180.75.58:8080}")
    private String swaggerHost;

    @Value("${swagger.protocol:http}")
    private String swaggerProtocol;

    /**
     * API 문서 기본 정보 설정
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(API_NAME)                    // API 문서 제목
                .description(API_DESCRIPTION)       // API 문서 설명
                .version(API_VERSION)               // API 버전
                .build();
    }

    /**
     * Swagger 문서 생성 설정
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)    // Swagger 2.0 사용
                .host(swaggerHost)                        // 환경별 호스트 설정
                .protocols(Set.of(swaggerProtocol))       // 환경별 프로토콜 설정
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))  // @RestController가 붙은 클래스만 문서화 대상으로 지정
                .paths(PathSelectors.any())  // 모든 경로 포함
                .build()
                .apiInfo(apiInfo())
                .securitySchemes(Collections.singletonList(apiKey()))  // JWT 인증 헤더 설정
                .securityContexts(Collections.singletonList(securityContext()));  // 보안 컨텍스트 설정
    }

    private ApiKey apiKey() {
        return new ApiKey("Bearer", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(
                        Collections.singletonList(
                                new SecurityReference("Bearer",
                                        new AuthorizationScope[] {
                                                new AuthorizationScope("global", "accessEverything")
                                        })
                        )
                )
                .forPaths(PathSelectors.any())
                .build();
    }
}