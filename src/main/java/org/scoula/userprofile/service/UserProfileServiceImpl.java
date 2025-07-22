package org.scoula.userprofile.service;

import lombok.RequiredArgsConstructor;
import org.scoula.userprofile.dto.UserProfileRequestDTO;
import org.scoula.userprofile.entity.UserProfile;
import org.scoula.userprofile.mapper.UserProfileMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    private final UserProfileMapper userProfileMapper;

    @Override
    public void saveUserProfile(Long userId, UserProfileRequestDTO DTO) {
        Long surplus = DTO.getIncome() - DTO.getFixedCost();

        //DB에 기존 프로필 존재 여부 확인 -> insert/update 분기처리
        UserProfile existing = userProfileMapper.findById(userId);

        UserProfile profile = UserProfile.builder()
                .memberId(userId)  //memberId는 클라이언트에게 입력X Principal로 db에서 받아서 처리.
                .username(DTO.getUsername())
                .age(DTO.getAge())
                .income(DTO.getIncome())
                .fixedCost(DTO.getFixedCost())
                .surplusAmount(surplus)
                .period(DTO.getPeriod())
                .purpose(DTO.getPurpose())
                .createdAt(LocalDateTime.now())
                .build();
        //분기처리.
        if (existing == null) {
            userProfileMapper.insertUserProfile(profile);
        } else {
            userProfileMapper.updateUserProfile(profile);
        }

    }

    @Override
    public UserProfile getUserProfile(Long memberId) {
        return userProfileMapper.findById(memberId);
    }
}
