package org.scoula.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialSignupCompleteDTO {

    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^01[0-9]{8,9}$", message = "올바른 전화번호 형식이 아닙니다.")
    private String phoneNumber;

    @NotNull(message = "생년월일은 필수입니다.")
    private Date birthDate;

    @NotBlank(message = "성별은 필수입니다.")
    @Pattern(regexp = "^(남|여)$", message = "성별은 '남' 또는 '여'만 입력 가능합니다.")
    private String gender;

    private Boolean receivePushNotification;
}
