package org.scoula.validation.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.scoula.member.service.MemberService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/validation")
//@RequiredArgsConstructor
public class ValidationApiController {
//
//    private final MemberService memberService;
//
//    @GetMapping("/check/email")
//    public ResponseEntity<?> checkEmail(@RequestParam String value) {
//        boolean available = memberService.isEmailAvailable(value);
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("available", available);
//        data.put("type", "email");
//
//        return ResponseEntity.ok(Map.of(
//                "success", true,
//                "message", available ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.",
//                "data", data
//        ));
//    }
//
//    @GetMapping("/check/nickname")
//    public ResponseEntity<?> checkNickname(@RequestParam String value) {
//        boolean available = memberService.isNicknameAvailable(value);
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("available", available);
//        data.put("type", "nickname");
//
//        return ResponseEntity.ok(Map.of(
//                "success", true,
//                "message", available ? "사용 가능한 닉네임입니다." : "이미 사용 중인 닉네임입니다.",
//                "data", data
//        ));
//    }
}
