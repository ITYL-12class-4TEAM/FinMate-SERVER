package org.scoula.auth.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserSession {
    private Long sessionId;
    private Long memberId;
    private String deviceId;
    private String userAgent;
    private String platform;
    private String refreshToken;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime logoutAt;
}
