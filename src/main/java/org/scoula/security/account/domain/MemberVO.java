package org.scoula.security.account.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberVO {
    private Long memberId;
    private String profileImage;
    private String username;
    private Integer level;
    private Long mainBadgeId;
    private String nickname;
    private String email;
    private String phoneNumber;
    private String password;
    private Date birthDate;
    private String gender;
    private Date createdAt;
    private Date updatedAt;
    private String role;
    private String socialType;
    private String socialId;
    private String accessToken;
    private String refreshToken;
    private Boolean isNewMember;
    private Boolean receivePushNotification;
//    private List<AuthVO> authList;        // 권한 목록, join 처리 필요

}