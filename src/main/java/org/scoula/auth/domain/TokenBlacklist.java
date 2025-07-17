package org.scoula.auth.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TokenBlacklist {
    private Long blacklistId;
    private Long memberId;
    private String token;
    private String tokenType; // "access" 또는 "refresh"
    private String reason;    // 예: "logout", "expired", "token_compromised"
    private LocalDateTime revokedAt;
    private LocalDateTime expiresAt;
}
