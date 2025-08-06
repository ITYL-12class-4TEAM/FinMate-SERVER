package org.scoula.auth.service;

import org.scoula.auth.dto.request.SocialSignupCompleteRequest;

public interface SocialAuthService {
    void completeSocialSignup(SocialSignupCompleteRequest request, String token);
    boolean checkNewMember(String token);
}
