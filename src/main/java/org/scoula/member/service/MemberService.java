package org.scoula.member.service;

public interface MemberService {
    boolean isEmailAvailable(String email);
    boolean isNicknameAvailable(String nickname);
}
