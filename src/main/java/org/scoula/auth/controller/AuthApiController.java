package org.scoula.auth.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.scoula.auth.dto.FindIdResponseDTO;
import org.scoula.auth.dto.TokenResponseDTO;
import org.scoula.member.service.SignupService;
import org.scoula.security.account.domain.MemberVO;
import org.scoula.security.util.JwtProcessor;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.common.service.RedisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthApiController {

    private final JwtProcessor jwtProcessor;
    private final MemberMapper memberMapper;
    private final RedisService redisService;
    private final SignupService signupService;
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDTO> refresh(@RequestBody RefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtProcessor.validateToken(refreshToken)) {
            return ResponseEntity.status(401)
                    .body(new TokenResponseDTO(false, "유효하지 않은 리프레시 토큰", null));
        }

        //  사용자 정보 추출
        String username = jwtProcessor.getUsername(refreshToken);
        Long memberId = memberMapper.findIdByUsername(username);

        //  Redis에 저장된 refresh 토큰과 일치하는지 확인
        String redisRefresh = memberMapper.getRefreshToken(memberId);
        if (!refreshToken.equals(redisRefresh)) {
            return ResponseEntity.status(401)
                    .body(new TokenResponseDTO(false, "리프레시 토큰이 일치하지 않습니다.", null));
        }

        //  새로운 access 토큰 발급
        String newAccessToken = jwtProcessor.generateAccessToken(memberId,username);

        //  Redis에 access 토큰 갱신
        redisService.saveAccessToken("ACCESS:" + memberId, newAccessToken);

        return ResponseEntity.ok(
                new TokenResponseDTO(true, "새로운 액세스 토큰이 발급되었습니다.",
                        new TokenResponseDTO.DataDTO(newAccessToken))
        );
    }
    @PostMapping("/find-id")
    public ResponseEntity<FindIdResponseDTO> findId(@RequestBody FindIdRequest request) {
        if (!request.isVerified()) {
            System.out.println("isVerified: " + request.isVerified());
            return ResponseEntity.badRequest().body(new FindIdResponseDTO(false, "본인인증이 필요합니다.", null));
        }
        String username = memberMapper.findUsernameByNameAndPhone(request.getName(), request.getPhoneNumber());
        if (username == null) {
            return ResponseEntity.badRequest().body(new FindIdResponseDTO(false, "일치하는 회원이 없습니다.", null));
        }
        return ResponseEntity.ok(new FindIdResponseDTO(true, "아이디 조회 성공", username));
    }
    @PostMapping("/find-password")
    public ResponseEntity<FindIdResponseDTO> findPassword(@RequestBody FindIdRequest request) {
        if (!request.isVerified()) {
            System.out.println("isVerified: " + request.isVerified());
            return ResponseEntity.badRequest().body(new FindIdResponseDTO(false, "본인인증이 필요합니다.", null));
        }
        String username = memberMapper.findUsernameByNameAndPhone(request.getName(), request.getPhoneNumber());
        if (username == null) {
            return ResponseEntity.badRequest().body(new FindIdResponseDTO(false, "일치하는 회원이 없습니다.", null));
        }
        return ResponseEntity.ok(new FindIdResponseDTO(true, "비밀번호를 다시 설정해주세요.", username));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<FindIdResponseDTO> resetPassword(@RequestBody ResetPasswordRequest request) {
        // 비밀번호 유효성 검사
        if (!signupService.isValidPassword(request.getNewPassword())) {
            return ResponseEntity.badRequest().body(
                    new FindIdResponseDTO(false, "비밀번호는 8자 이상, 영문/숫자/특수문자를 포함해야 합니다.", null)
            );
        }
        if (!request.getNewPassword().equals(request.getNewPasswordCheck())) {
            return ResponseEntity.badRequest().body(
                    new FindIdResponseDTO(false, "비밀번호가 일치하지 않습니다.", null)
            );
        }
        MemberVO member = memberMapper.selectByEmail(request.getUsername());
        Long memberId = member.getMemberId();
        // 비밀번호 암호화 및 저장
         encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(request.getNewPassword());
        memberMapper.updatePassword(memberId, encodedPassword);

        return ResponseEntity.ok(new FindIdResponseDTO(true, "비밀번호가 성공적으로 변경되었습니다.", null));
    }

    // 요청 DTO
    public static class ResetPasswordRequest {
        private String username;
        private String newPassword;
        private String newPasswordCheck;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
        public String getNewPasswordCheck() { return newPasswordCheck; }
        public void setNewPasswordCheck(String newPasswordCheck) { this.newPasswordCheck = newPasswordCheck; }
    }

    // 요청 DTO
    public static class RefreshRequest {
        private String refreshToken;
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }
    public static class FindIdRequest {
        private String name;
        private String phoneNumber;
        @JsonProperty("isVerified")
        private boolean isVerified;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public boolean isVerified() { return isVerified; }
        public void setVerified(boolean verified) { isVerified = verified; }
    }
}