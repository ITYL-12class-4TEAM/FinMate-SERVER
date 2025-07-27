package org.scoula.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.member.dto.MemberDTO;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.security.account.domain.CustomUser;
import org.scoula.security.account.domain.MemberVO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Log4j2
public class MemberApiController {

    private final MemberMapper memberMapper;

    @GetMapping("/me")
    public ResponseEntity<MemberDTO> getCurrentUser(
            @AuthenticationPrincipal CustomUser userDetails
    ) {
        String email = userDetails.getUsername(); // CustomUser는 email을 username으로 사용
        MemberVO memberVO =  memberMapper.selectByEmail(email);

        MemberDTO response = MemberDTO.of(memberVO);

        return ResponseEntity.ok(response);
    }
}