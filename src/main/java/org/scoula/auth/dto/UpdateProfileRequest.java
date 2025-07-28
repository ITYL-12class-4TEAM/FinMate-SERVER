package org.scoula.auth.dto;

public class UpdateProfileRequest {
    private Long memberId;
    private String nickname;
    private Boolean receivePushNotification;

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public Boolean getReceivePushNotification() { return receivePushNotification; }
    public void setReceivePushNotification(Boolean receivePushNotification) { this.receivePushNotification = receivePushNotification; }

}
