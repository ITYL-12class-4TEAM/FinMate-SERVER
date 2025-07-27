package org.scoula.member.service.impl;

import lombok.RequiredArgsConstructor;
import org.scoula.member.dto.MemberDTO;
import org.scoula.member.exception.MemberNotFoundException;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.member.service.MemberService;
import org.scoula.response.ResponseCode;
import org.scoula.security.account.domain.MemberVO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberMapper memberMapper;

    @Override
    public boolean isEmailAvailable(String email) {
        return memberMapper.selectByEmail(email) == null;
    }

    @Override
    public boolean isNicknameAvailable(String nickname) {
        return memberMapper.selectByNickname(nickname) == null;
    }

    @Override
    public MemberDTO getCurrentUser(String email) {
        MemberVO memberVO = memberMapper.selectByEmail(email);
        if (memberVO == null) {
            throw new MemberNotFoundException(ResponseCode.MEMBER_NOT_FOUND);
        }
        return MemberDTO.of(memberVO);
    }

}
