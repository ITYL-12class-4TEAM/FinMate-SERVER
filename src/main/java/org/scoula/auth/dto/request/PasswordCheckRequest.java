package org.scoula.auth.dto.request;


import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class PasswordCheckRequest {
    @NotBlank(message = "비밀번호는 입력해주세요.")
    private String password;
}