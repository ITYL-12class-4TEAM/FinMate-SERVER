package org.scoula.member.service;

import org.scoula.member.dto.ChangePasswordDTO;
import org.scoula.member.dto.MemberDTO;
import org.scoula.member.dto.MemberJoinDTO;
import org.scoula.member.dto.MemberUpdateDTO;

public interface MemberService {
    boolean isEmailAvailable(String email);
    boolean isNicknameAvailable(String nickname);

    MemberDTO get(String username);

    MemberDTO join(MemberJoinDTO member);

    MemberDTO update(MemberUpdateDTO member);

    void changePassword(ChangePasswordDTO changePassword);
}
