package org.scoula.preinfo.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.scoula.preinfo.dto.PreInfoViewDTO;
import org.scoula.preinfo.entity.PreInformation;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.scoula.preinfo.dto.PreInfoRequestDTO;
import org.scoula.preinfo.dto.PreInfoResponseDTO;
import org.scoula.preinfo.service.PreInfoService;
import org.scoula.security.util.JwtProcessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pre-info")
@Api(tags = "사전 정보 입력 API", description = "투자성향 검사 전에 입력하는 기본 정보를 등록합니다.")
public class PreInfoController {
    private final PreInfoService preInfoService;
    private final JwtProcessor jwtProcessor;

    @PostMapping
    @ApiOperation(value = "사전 정보 등록", notes = "투자성향 분석 전, 로그인한 사용자의 기본 정보를 등록합니다.")
    public ApiResponse<PreInfoResponseDTO> submitPreInfo(
            @ApiParam(value = "사전 정보 요청 DTO", required = true)
            @RequestBody PreInfoRequestDTO DTO,
            HttpServletRequest request
    ) {
        // Authorization 헤더에서 토큰 추출(memberId 추출용)
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return ApiResponse.fail(ResponseCode.AUTH_TOKEN_NOT_FOUND);
        }
        String token = header.substring(7);

        try {
            Long memberId = jwtProcessor.getMemberId(token);    //JWT에서 memberId 추출
            PreInfoResponseDTO response = preInfoService.savePreInfoAndResponse(memberId, DTO);
            return ApiResponse.success(ResponseCode.PREINFO_USER_PROFILE_SUBMIT_SUCCESS, response);
        } catch (ExpiredJwtException e){
            return ApiResponse.fail(ResponseCode.AUTH_TOKEN_EXPIRED);
        } catch (JwtException e){
            return ApiResponse.fail(ResponseCode.AUTH_TOKEN_INVALID);
        }
    }

    @GetMapping("/my")
    @ApiOperation(value = "내 사전 정보 조회", notes = "로그인한 사용자의 사전 정보를 조회합니다.")
    public ApiResponse<PreInfoViewDTO> getMyPreInfo(HttpServletRequest request
    ) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return ApiResponse.fail(ResponseCode.AUTH_TOKEN_NOT_FOUND);
        }

        try {
            String token = header.substring(7);
            Long memberId = jwtProcessor.getMemberId(token);
            PreInformation entity = preInfoService.getUserProfile(memberId);

            if (entity == null) {
                return ApiResponse.fail(ResponseCode.PREINFO_NOT_FOUND, "등록된 사전 정보가 없습니다.");
            }

            PreInfoViewDTO dto = PreInfoViewDTO.from(entity);
            return ApiResponse.success(ResponseCode.PREINFO_RETRIEVED, dto);
        } catch (JwtException e) {
            return ApiResponse.fail(ResponseCode.AUTH_TOKEN_INVALID);
        }
    }

    @GetMapping("/admin/{preInfoId}")
    @ApiOperation(value = "사전 정보 단건 조회", notes = "preInfoId로 특정 사용자의 사전 정보를 조회합니다.")
    public ApiResponse<PreInfoViewDTO> getPreInfoById(
            @ApiParam(value = "사전정보 고유 ID", required = true, example = "PRE_7_20250726")
            @PathVariable String preInfoId
    ) {
        PreInformation entity = preInfoService.getPreInfoById(preInfoId);
        if (entity == null) {
            return ApiResponse.fail(ResponseCode.PREINFO_NOT_FOUND);
        }

        return ApiResponse.success(ResponseCode.PREINFO_RETRIEVED, PreInfoViewDTO.from(entity));
    }
}
