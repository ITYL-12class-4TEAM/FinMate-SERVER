package org.scoula.member.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@ApiModel(description = "회원가입 요청 DTO")
public class RegisterDTO {

    @ApiModelProperty(value = "사용자 아이디", example = "testuser", required = true)
    @NotBlank(message = "username은 필수입니다.")
    private String username;

    @ApiModelProperty(value = "비밀번호 (영문, 숫자, 특수문자 포함 8자 이상)", example = "Test@1234", required = true)
    @NotBlank(message = "password는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;

    @ApiModelProperty(value = "비밀번호 확인", example = "Test@1234", required = true)
    @NotBlank(message = "passwordCheck는 필수입니다.")
    private String passwordCheck;

    @ApiModelProperty(value = "닉네임", example = "테스트닉네임", required = true)
    @NotBlank(message = "nickname은 필수입니다.")
    private String nickname;

    @ApiModelProperty(value = "이메일 주소", example = "test@example.com", required = true)
    @NotBlank(message = "email은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @ApiModelProperty(value = "휴대폰 번호 ('-' 제외 숫자만 입력)", example = "01012345678", required = true)
    @NotBlank(message = "phoneNumber는 필수입니다.")
    @Pattern(regexp = "^\\d{10,11}$", message = "휴대폰 번호는 10~11자리 숫자여야 합니다.")
    private String phoneNumber;

    @ApiModelProperty(value = "생년월일 (yyyy-MM-dd 형식)", example = "1990-01-01", required = true)
    @NotBlank(message = "birthDate는 필수입니다.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "생년월일 형식은 yyyy-MM-dd 입니다.")
    private String birthDate;

    @ApiModelProperty(value = "성별 (남 또는 여)", example = "남", required = true)
    @NotBlank(message = "gender는 필수입니다.")
    @Pattern(regexp = "^(남|여)$", message = "성별은 '남' 또는 '여'만 입력 가능합니다.")
    private String gender;

    @ApiModelProperty(value = "필수 약관 동의 1", example = "true", required = true, notes = "서비스 이용약관 동의")
    @NotNull(message = "termsRequired1은 필수입니다.")
    private Boolean termsRequired1;

    @ApiModelProperty(value = "필수 약관 동의 2", example = "true", required = true, notes = "개인정보 처리방침 동의")
    @NotNull(message = "termsRequired2은 필수입니다.")
    private Boolean termsRequired2;

    @ApiModelProperty(value = "푸시 알림 수신 동의 여부 (선택)", example = "true", required = false, notes = "푸시 알림 수신 동의")
    private Boolean receive_push_notification;
}
