package org.scoula.member.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.member.dto.ValidationResponseDTO;
import org.scoula.member.mapper.MemberMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/validation/check")
@RequiredArgsConstructor
public class ValidationApiController {
    private final MemberMapper memberMapper;

    @GetMapping("/email")
    public ValidationResponseDTO checkEmail(@RequestParam String email) {
        boolean available = memberMapper.selectByEmail(email) == null;
        return new ValidationResponseDTO(
                true,
                available ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.",
                new ValidationResponseDTO.DataDTO(available, "email")
        );
    }

    @GetMapping("/nickname")
    public ValidationResponseDTO checkNickname(@RequestParam String nickname) {
        boolean available = memberMapper.selectByNickname(nickname) == null;
        return new ValidationResponseDTO(
                true,
                available ? "사용 가능한 닉네임입니다." : "이미 사용 중인 닉네임입니다.",
                new ValidationResponseDTO.DataDTO(available, "nickname")
        );
    }
}