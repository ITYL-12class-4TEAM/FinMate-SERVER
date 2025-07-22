package org.scoula.preinfo.service;

import org.scoula.preinfo.dto.PreInfoRequestDTO;
import org.scoula.preinfo.dto.PreInfoResponseDTO;
import org.scoula.preinfo.entity.PreInformation;

public interface PreInfoService {
    PreInformation getUserProfile(Long memberId);
    PreInfoResponseDTO savePreInfoAndResponse(Long userId, PreInfoRequestDTO dto);
}
