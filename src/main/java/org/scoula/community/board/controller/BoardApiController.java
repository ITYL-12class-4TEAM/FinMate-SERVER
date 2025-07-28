package org.scoula.community.board.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.community.board.dto.BoardResponseDTO;
import org.scoula.community.board.service.BoardService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
@Log4j2
@Api(tags = "게시판 API")
public class BoardApiController {

    private final BoardService service;

    @ApiOperation(
            value = "게시판 리스트 조회",
            notes = "등록된 모든 게시판을 조회합니다. ")
    @GetMapping("")
    public ApiResponse<List<BoardResponseDTO>> getList() {
        return ApiResponse.success(ResponseCode.BOARD_LIST_SUCCESS, service.getList());
    }

    @ApiOperation(
            value = "게시판 생성",
            notes = "요청 바디에 게시판 정보를 JSON 형식으로 전달하여 새로운 게시글을 생성합니다. " +
                    "필수 필드는 제목(title), 작성자(writer), 게시판 타입(boardType)이며, " +
                    "게시판 타입은 'FREE', 'HOT', 'NOTICE', 'CUSTOM' 중 하나여야 합니다. " +
                    "성공 시 생성된 게시글 정보를 반환합니다."
    )
    @PostMapping("")
    public ApiResponse<BoardResponseDTO> create(@RequestBody BoardResponseDTO dto) {
        return ApiResponse.success(ResponseCode.BOARD_CREATE_SUCCESS, service.create(dto));
    }

}
