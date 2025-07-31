package org.scoula.security.account.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResultDTO {
    private String accessToken;
    private String refreshToken;
    private Long memberId;
    private UserInfoDTO userInfo;
    private boolean isNewMember;

}

