package org.scoula.preinfo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.preinfo.entity.PreInformation;

@Mapper
public interface PreInfoMapper {
    //사용자 사전정보 입력 - Create
    int insertPreInfo(PreInformation preInformation);
    //사용자 사전정보 조회 - Read (By memberId)
    PreInformation findByMemberId(Long memberId);
    //사용자 사전정보 조회 - Read (By preInfoId)
    PreInformation findByPreInfoId(String preInfoId);
    //사용자 사전정보 수정 - Update (같은 memberId가 있으면 최신 정보로 수정)
    int updatePreInfo(PreInformation preInformation);
    //필수정보이므로 삭제기능은 구현X -> 추후 개인정보관리정책에 따른 '기간에의한 자동삭제' 추가 가능성

}
