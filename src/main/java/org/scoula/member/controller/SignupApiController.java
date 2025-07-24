package org.scoula.member.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.scoula.member.dto.RegisterDTO;
import org.scoula.member.dto.SignupResponseDTO;
import org.scoula.member.service.SignupService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
@Api(tags = "회원가입 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SignupApiController {
    private final SignupService signupService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> register(@Validated @RequestBody RegisterDTO dto) {
        SignupResponseDTO response = signupService.register(dto, dto.getPhoneNumber());
        return ResponseEntity.ok(response);
    }
}