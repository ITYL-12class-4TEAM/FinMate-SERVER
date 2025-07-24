package org.scoula.member.dto;

import lombok.Data;
import javax.validation.constraints.*;
@Data
public class RegisterDTO {
    private String username;
    private String password;
    private String passwordCheck;
    private String nickname;
    private String email;
    private String phoneNumber;//"-제외
    private String birthDate; // "yyyy-MM-dd"
    private String gender;
    private Boolean termsRequired1;
    private Boolean termsRequired2;
    private Boolean receive_push_notification;



}
