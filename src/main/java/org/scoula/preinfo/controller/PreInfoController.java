package org.scoula.preinfo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.scoula.preinfo.dto.PreInfoRequestDTO;
import org.scoula.preinfo.dto.PreInfoResponseDTO;
import org.scoula.preinfo.service.PreInfoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pre-info")
@Api(tags = "사전 정보 입력 API")
public class PreInfoController {
    private final PreInfoService preInfoService;

    @PostMapping
    @ApiOperation(value = "사전 정보 등록", notes = "로그인한 사용자의 기본 정보를 등록합니다.")
    public ApiResponse<PreInfoResponseDTO> submitPreInfo(@RequestBody PreInfoRequestDTO DTO, Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        PreInfoResponseDTO response = preInfoService.savePreInfoAndResponse(userId, DTO);
        return ApiResponse.success(ResponseCode.PREINFO_USER_PROFILE_SUBMIT_SUCCESS);
    }
}
