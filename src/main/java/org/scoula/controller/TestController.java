package org.scoula.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {
    @Value("${jwt.secret_key:NOT_FOUND}")
    private String secretKey;
    @Value("${jdbc.url}")
    private String jdbcUrl;

    @GetMapping("/test")
    @ResponseBody
    public String testJdbcUrl() {
        System.out.println("[test] " + secretKey + ", " + jdbcUrl);
        return "jdbc.url = " + jdbcUrl;
    }

}
