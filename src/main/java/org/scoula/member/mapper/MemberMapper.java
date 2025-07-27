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
MemberVO selectByEmail(String email);
MemberVO selectByNickname(String nickname);  // 회원 정보 조회
Long findIdByUsername(String username);   // id 중복 체크시 사용

String getRefreshToken(@Param("memberId") Long memberId);

String findUsernameByNameAndPhone(@Param("username") String username,
                                  @Param("phoneNumber") String phoneNumber);

void insert(MemberVO member);  // 회원 정보 추가
int updateProfile(MemberVO member);
//    int insertAuth(AuthVO auth);        // 회원 권한 정보 추가

//    int update(MemberVO member);
// MemberMapper.java
void updateTokens(@Param("username") String username,
                  @Param("refreshToken") String refreshToken);

void clearRefreshToken(@Param("username") String username);

int updatePassword(@Param("memberId") Long memberId,
                   @Param("Password") String encodedPassword);
long getMemberIdByEmail(@Param("email") String email);

int deleteMember(@Param("memberId") Long memberId);

void updateProfileImage(@Param("memberId") Long memberId,
                        @Param("profileImage") String profileImage);
}
