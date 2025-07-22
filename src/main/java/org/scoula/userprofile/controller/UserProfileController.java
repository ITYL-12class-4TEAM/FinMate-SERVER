package org.scoula.userprofile.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.scoula.userprofile.dto.UserProfileRequestDTO;
import org.scoula.userprofile.service.UserProfileService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-profile")
@Api(tags = "사용자 프로필 API")
public class UserProfileController {
    private final UserProfileService userProfileService;

    @PostMapping
    @ApiOperation(value = "사용자 프로필 등록", notes = "로그인한 사용자의 기본 정보를 등록합니다.")
    public ApiResponse<?> submitUserProfile(@RequestBody UserProfileRequestDTO DTO, Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        userProfileService.saveUserProfile(userId, DTO);
        return ApiResponse.success(ResponseCode.USER_PROFILE_SUBMIT_SUCCESS);
    }
}
