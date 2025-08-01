package org.scoula.mypage.util;

import lombok.RequiredArgsConstructor;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.response.ResponseCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.scoula.mypage.favorite.exception.AuthenticationException;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final MemberMapper memberMapper;

    /**
     * 현재 로그인한 사용자의 ID를 Long 타입으로 반환
     * @return 사용자 ID
     * @throws AuthenticationException 인증 정보를 가져오지 못하거나 사용자를 찾을 수 없는 경우
     */
    public Long getCurrentUserIdAsLong() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Long memberId = memberMapper.getMemberIdByEmail(email);

            if (memberId == null) {
                throw new AuthenticationException(ResponseCode.MEMBER_NOT_FOUND);
            }

            return memberId;
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationException(ResponseCode.AUTHENTICATION_FAILED);
        }
    }

    /**
     * 현재 로그인한 사용자의 이메일 반환
     * @return 사용자 이메일
     * @throws AuthenticationException 인증 정보를 가져오지 못하는 경우
     */
    public String getCurrentUserEmail() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            throw new AuthenticationException(ResponseCode.AUTHENTICATION_FAILED);
        }
    }
}