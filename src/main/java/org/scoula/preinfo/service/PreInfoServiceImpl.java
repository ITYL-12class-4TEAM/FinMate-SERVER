package org.scoula.preinfo.service;

import lombok.RequiredArgsConstructor;
import org.scoula.preinfo.dto.PreInfoRequestDTO;
import org.scoula.preinfo.entity.PreInformation;
import org.scoula.preinfo.mapper.PreInfoMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PreInfoServiceImpl implements PreInfoService {
    private final PreInfoMapper preInfoMapper;

    @Override
    public void saveUserProfile(Long userId, PreInfoRequestDTO DTO) {
        Long surplus = DTO.getIncome() - DTO.getFixedCost();

        //DB에 기존 프로필 존재 여부 확인 -> insert/update 분기처리
        PreInformation existing = preInfoMapper.findById(userId);

        PreInformation profile = PreInformation.builder()
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
            preInfoMapper.insertUserProfile(profile);
        } else {
            preInfoMapper.updateUserProfile(profile);
        }

    }

    @Override
    public PreInformation getUserProfile(Long memberId) {
        return preInfoMapper.findById(memberId);
    }
}
