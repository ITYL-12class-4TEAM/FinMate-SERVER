package org.scoula.preinfo.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.scoula.preinfo.dto.PreInfoRequestDTO;
import org.scoula.preinfo.dto.PreInfoResponseDTO;
import org.scoula.preinfo.service.PreInfoService;
import org.scoula.security.util.JwtProcessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

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


}
