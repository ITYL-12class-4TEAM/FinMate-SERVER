package org.scoula.products.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "테스트 API")
@RequestMapping("/api/test")
public class TestController {

    @ApiOperation("테스트 API")
    @GetMapping
    public String test() {
        return "테스트 성공";
    }
}