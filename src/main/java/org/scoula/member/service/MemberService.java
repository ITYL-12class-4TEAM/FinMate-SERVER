package org.scoula.member.service;

import org.scoula.member.dto.MemberDTO;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {
    boolean isEmailAvailable(String email);
    boolean isNicknameAvailable(String nickname);
    MemberDTO getCurrentUser(String email);
    String uploadProfileImage(String username, MultipartFile file);

}
