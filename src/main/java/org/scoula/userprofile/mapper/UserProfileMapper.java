package org.scoula.userprofile.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.userprofile.entity.UserProfile;

@Mapper
public interface UserProfileMapper {
    int insertUserProfile(UserProfile userProfile);
    UserProfile findById(Long memberId);
    int updateUserProfile(UserProfile userProfile);

}
