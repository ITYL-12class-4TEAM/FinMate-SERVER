package org.scoula.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.member.exception.MemberNotFoundException;
import org.scoula.member.exception.MemberDeletedException;
import org.scoula.response.ResponseCode;
import org.scoula.security.account.domain.CustomUser;
import org.scoula.security.account.domain.MemberVO;
import org.scoula.member.mapper.MemberMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@Log4j2
@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberMapper memberMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        MemberVO vo = memberMapper.selectByEmail(email);
        log.info("[DEBUG] 로그인 요청 이메일: {}", email);

        if (vo == null) {
            throw new MemberNotFoundException(ResponseCode.MEMBER_NOT_FOUND);
        }

        // 탈퇴된 회원 체크
        if ("DELETED".equals(vo.getStatus())) {
            throw new MemberDeletedException(ResponseCode.MEMBER_DELETED);
        }


        return new CustomUser(vo);
    }

}
