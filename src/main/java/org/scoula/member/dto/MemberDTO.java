package org.scoula.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.security.account.domain.MemberVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {
    private Long memberId;
    private String username;
    private String nickname;
    private String email;
    private String profileImage;
    private Integer level;
    private Long mainBadgeId;
    private String phoneNumber;
    private Date birthDate;
    private String gender;
    private String socialType;
    private String socialId;
    private String role;
    private Boolean receivePushNotification;
    private Date createdAt;
    private Date updatedAt;

    private MultipartFile avatar;
    private List<String> authList;        // 권한 목록, join 처리 필요

    public static MemberDTO of(MemberVO m) {
        return MemberDTO.builder()
                .memberId(m.getMemberId())
                .username(m.getUsername())
                .nickname(m.getNickname())
                .email(m.getEmail())
                .profileImage(m.getProfileImage())
                .level(m.getLevel())
                .mainBadgeId(m.getMainBadgeId())
                .phoneNumber(m.getPhoneNumber())
                .birthDate(m.getBirthDate())
                .gender(m.getGender())
                .role(m.getRole())
                .receivePushNotification(m.getReceivePushNotification())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }

    public MemberVO toVO() {
        return MemberVO.builder()
                .memberId(memberId)
                .username(username)
                .nickname(nickname)
                .email(email)
                .profileImage(profileImage)
                .level(level)
                .mainBadgeId(mainBadgeId)
                .phoneNumber(phoneNumber)
                .birthDate(birthDate)
                .gender(gender)
                .role(role)
                .receivePushNotification(receivePushNotification)
                .build();
    }
}
