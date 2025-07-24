package org.scoula.member.service.impl;

import lombok.RequiredArgsConstructor;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.member.service.MemberService;
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
}
