package org.scoula.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LogoutApiController {

    @PostMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            session.invalidate(); // 세션 만료(로그아웃)
            result.put("success", true);
            result.put("message", "로그아웃 되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "로그아웃 중 오류가 발생했습니다.");
        }
        return result;
    }
}
