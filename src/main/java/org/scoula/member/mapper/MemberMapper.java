package org.scoula.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.member.dto.ChangePasswordDTO;
import org.scoula.member.dto.MemberDTO;
import org.scoula.security.account.domain.AuthVO;
import org.scoula.security.account.domain.MemberVO;

@Mapper
public interface MemberMapper {
//    MemberVO get(String username);
//
//    MemberVO findByUsername(String username);    // id 중복 체크시 사용
//    MemberVO selectByEmail(String email);
//    MemberVO selectByNickname(String nickname);

//    int insert(MemberVO member);  // 회원 정보 추가

//    int insertAuth(AuthVO auth);        // 회원 권한 정보 추가

//    int update(MemberVO member);
// MemberMapper.java
void updateTokens(@Param("username") String username,
                  @Param("refreshToken") String refreshToken);

//    int updatePassword(ChangePasswordDTO changePasswordDTO);

}
