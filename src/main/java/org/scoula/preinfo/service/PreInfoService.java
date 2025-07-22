package org.scoula.preinfo.service;

import org.scoula.preinfo.dto.PreInfoRequestDTO;
import org.scoula.preinfo.entity.PreInformation;

public interface PreInfoService {
    void saveUserProfile(Long member_id, PreInfoRequestDTO DTO);
    PreInformation getUserProfile(Long memberId);
}
