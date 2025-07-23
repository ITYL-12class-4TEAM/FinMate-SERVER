package org.scoula.auth.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.scoula.security.account.dto.LoginDTO;
import org.springframework.web.bind.annotation.*;

@Api(tags = "로그인 API")
@RestController
@RequestMapping("/api/auth")
public class LoginApiController {

    @ApiOperation(value = "로그인", notes = "이메일과 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public void login(@RequestBody LoginDTO loginDTO) {

    }
}