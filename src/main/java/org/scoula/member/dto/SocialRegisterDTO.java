package org.scoula.member.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@ApiModel(description = "소셜 로그인 회원가입 요청 DTO")
public class SocialRegisterDTO {

    @ApiModelProperty(value = "이메일", example = "user@gmail.com", readOnly = true)
    private String email;

    @ApiModelProperty(value = "이름", example = "홍길동", readOnly = true)
    private String username;

    @ApiModelProperty(value = "닉네임", example = "길동이", required = true)
    @NotBlank(message = "닉네임은 필수입니다")
    private String nickname;

    @ApiModelProperty(value = "생년월일 (YYYY-MM-DD)", example = "1990-01-01", required = true)
    @NotBlank(message = "생년월일은 필수입니다")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "생년월일 형식이 올바르지 않습니다 (YYYY-MM-DD)")
    private String birthDate;

    @ApiModelProperty(value = "성별 (남/여)", example = "남", required = true)
    @NotBlank(message = "성별은 필수입니다")
    @Pattern(regexp = "^(남|여)$", message = "성별은 남 또는 여만 가능합니다")
    private String gender;

    @ApiModelProperty(value = "푸시 알림 수신 여부", example = "true")
    private Boolean receivePushNotification = false;

    @ApiModelProperty(value = "프로필 이미지 URL (구글에서 받은 정보, 자동 매핑)", example = "https://...", readOnly = true)
    private String profileImage;
}
