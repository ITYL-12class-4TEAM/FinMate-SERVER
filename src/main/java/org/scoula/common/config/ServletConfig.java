package org.scoula.common.config;

import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@EnableWebMvc
@ComponentScan(basePackages = {
        "org.scoula.common.exception",
        "org.scoula.chatgpt.controller",
        "org.scoula.chatgpt.service",
        "org.scoula.chatbot.controller",
        "org.scoula.chatbot.session",
        "org.scoula.community.board.service",
        "org.scoula.community.board.controller"
}) // Spring MVC용 컴포넌트 등록을 위한 스캔 패키지
public class ServletConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 기본 리소스 설정
        registry
                .addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");

        // Swagger UI 리소스를 위한 핸들러 설정
        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        // Swagger WebJar 리소스 설정 (Bootstrap, jQuery 등)
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        // Swagger 메타데이터 리소스 설정
        registry.addResourceHandler("/swagger-resources/**")
                .addResourceLocations("classpath:/META-INF/resources/");

        // API 문서 JSON 엔드포인트 설정
        registry.addResourceHandler("/v2/api-docs")
                .addResourceLocations("classpath:/META-INF/resources/");
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setViewClass(JstlView.class);           // JSTL 지원 활성화
        bean.setPrefix("/WEB-INF/views/");           // JSP 파일 기본 경로
        bean.setSuffix(".jsp");                      // JSP 파일 확장자
        registry.viewResolver(bean);                 // ViewResolver 등록
    }

    // 📍 Servlet 3.0 파일 업로드 설정
    @Bean
    public MultipartResolver multipartResolver() {
        StandardServletMultipartResolver resolver =
                new StandardServletMultipartResolver();
        return resolver;
    }
    @Bean
    public static PropertySourcesPlaceholderConfigurer servletPropertyConfig() throws IOException, IOException {
        return RootConfig.propertyConfig();
    }
}
