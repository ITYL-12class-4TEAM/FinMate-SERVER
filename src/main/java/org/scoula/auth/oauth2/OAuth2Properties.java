package org.scoula.auth.oauth2;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class OAuth2Properties {
    @Value("${oauth2.google.client-id}")
    private String googleClientId;

    @Value("${oauth2.google.client-secret}")
    private String googleClientSecret;

    @Value("${oauth2.google.redirect-uri:http://localhost:8080/login/oauth2/code/google}")
    private String googleRedirectUri;

    @Value("${oauth2.kakao.client-id}")
    private String kakaoClientId;

    @Value("${oauth2.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${oauth2.kakao.redirect-uri:http://localhost:8080/login/oauth2/code/kakao}")
    private String kakaoRedirectUri;

    // getter 메서드들
    public Google getGoogle() {
        return new Google(googleClientId, googleClientSecret, googleRedirectUri);
    }

    public Kakao getKakao() {
        return new Kakao(kakaoClientId, kakaoClientSecret, kakaoRedirectUri);
    }

    @Data
    public static class Google {
        private String clientId;
        private String clientSecret;
        private String redirectUri;

        public Google(String clientId, String clientSecret, String redirectUri) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.redirectUri = redirectUri;
        }
    }

    @Data
    public static class Kakao {
        private String clientId;
        private String clientSecret;
        private String redirectUri;

        public Kakao(String clientId, String clientSecret, String redirectUri) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.redirectUri = redirectUri;
        }
    }
}