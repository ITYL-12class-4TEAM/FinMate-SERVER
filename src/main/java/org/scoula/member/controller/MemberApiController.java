package org.scoula.member.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.member.dto.MemberDTO;
import org.scoula.member.service.MemberService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.scoula.security.account.domain.CustomUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "회원 정보 API" , description = "현재 사용자 정보 조회 및 프로필 사진 업로드 API")
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Log4j2
public class MemberApiController {

    private final MemberService memberService;

    @ApiOperation(
            value = "현재 로그인 사용자 정보 조회",
            notes = "로그인한 사용자의 상세 정보를 조회합니다."
    )
    @GetMapping("/me")
    public ApiResponse<MemberDTO> getCurrentUser(@AuthenticationPrincipal CustomUser userDetails) {
        MemberDTO memberInfo = memberService.getCurrentUser(userDetails.getUsername());
        return ApiResponse.success(ResponseCode.MEMBER_INFO_SUCCESS, memberInfo);
    }

    @ApiOperation(
            value = "프로필 사진 업로드",
            notes = "사용자의 프로필 사진을 업로드합니다. multipart/form-data 형식으로 전송해야 합니다."
    )
    @PostMapping("/profile-image")
    public ApiResponse<String> uploadProfileImage(
            @AuthenticationPrincipal CustomUser userDetails,
            @RequestParam("file") MultipartFile file) {

        String imageUrl = memberService.uploadProfileImage(userDetails.getUsername(), file);
        return ApiResponse.success(ResponseCode.PROFILE_IMAGE_UPLOAD_SUCCESS, imageUrl);
    }
}