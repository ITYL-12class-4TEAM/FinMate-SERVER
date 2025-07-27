package org.scoula.member.service;

import org.scoula.member.dto.MemberDTO;

public interface MemberService {
    boolean isEmailAvailable(String email);
    boolean isNicknameAvailable(String nickname);
    MemberDTO getCurrentUser(String email);
}
