package org.scoula.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AppProperties {

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @Value("${app.frontend.oauth2.redirect-url}")
    private String frontendOAuth2RedirectUrl;

    @Value("${app.frontend.login.error-url}")
    private String frontendLoginErrorUrl;

    @Value("${app.backend.base-url}")
    private String backendBaseUrl;
}