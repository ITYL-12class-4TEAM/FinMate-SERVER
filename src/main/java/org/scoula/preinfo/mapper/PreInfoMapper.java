package org.scoula.preinfo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.preinfo.entity.PreInformation;

@Mapper
public interface PreInfoMapper {
    int insertUserProfile(PreInformation preInformation);
    PreInformation findById(Long memberId);
    int updateUserProfile(PreInformation preInformation);

}
