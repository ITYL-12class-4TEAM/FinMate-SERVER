package org.scoula.userprofile.service;

import org.scoula.userprofile.dto.UserProfileRequestDTO;
import org.scoula.userprofile.entity.UserProfile;

public interface UserProfileService {
    void saveUserProfile(Long member_id, UserProfileRequestDTO DTO);
    UserProfile getUserProfile(Long memberId);
}
