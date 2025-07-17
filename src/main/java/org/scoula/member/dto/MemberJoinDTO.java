package org.scoula.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.security.account.domain.MemberVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberJoinDTO {
    private String username;
    private String nickname;
    private String email;
    private String password;
    private String phoneNumber;
    private Date birthDate;
    private String gender;

    private MultipartFile avatar;

    public MemberVO toVO() {
        return MemberVO.builder()
                .username(username)
                .nickname(nickname)
                .email(email)
                .password(password)
                .phoneNumber(phoneNumber)
                .birthDate(birthDate)
                .gender(gender)
                .build();
    }
}
