package org.scoula.auth.dto;

public class ResetPasswordRequest {
    private String username;
    private String newPassword;
    private String newPasswordCheck;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public String getNewPasswordCheck() { return newPasswordCheck; }
    public void setNewPasswordCheck(String newPasswordCheck) { this.newPasswordCheck = newPasswordCheck; }
}
