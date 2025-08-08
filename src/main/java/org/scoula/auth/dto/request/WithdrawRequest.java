package org.scoula.auth.dto.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class WithdrawRequest {
    @NotBlank(message = "이메일은 필수입니다.")
    private String username;

    private String withdrawReason;

    @NotNull(message = "탈퇴 동의는 필수입니다.")
    private Boolean agreeToWithdraw;
    // 내부적으로만 사용
}