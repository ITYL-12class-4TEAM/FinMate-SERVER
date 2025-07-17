package org.scoula.chatgpt.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.scoula.chatgpt.dto.gpt.ChatMessage;
import org.scoula.chatgpt.dto.product.FinancialProductGptRequest;
import org.scoula.chatgpt.service.ChatGptService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "ChatGPT 금융 상품 API")
@RequestMapping("/api")
public class ChatGptController {

    private final ChatGptService chatGptService;

    @Value("${chatgpt.summary-prompt}")
    private String summaryPrompt;

    @Value("${chatgpt.compare-prompt}")
    private String comparePrompt;

    public ChatGptController(ChatGptService chatGptService) {
        this.chatGptService = chatGptService;
    }

    @ApiOperation(value = "금융 상품 요약 요청",
            notes = "금융 상품 정보를 입력받아, 사회초년생 및 금융 초보자를 위한 쉬운 요약을 반환합니다.")
    @PostMapping("/chat/summary")
    public org.scoula.response.ApiResponse<?> summarize(@RequestBody FinancialProductGptRequest product) {
        String content = product.toString() + "\n\n" + summaryPrompt;
        String response = chatGptService.summarize(List.of(new ChatMessage(content)));
        return ApiResponse.success(ResponseCode.CHATGPT_SUMMARY_SUCCESS, response);
    }

    @ApiOperation(value = "금융 상품 비교 요청",
            notes = "복수의 금융 상품 정보를 입력받아, 각각의 상품을 비교하고 어떤 사람에게 더 적합한지 설명합니다.")
    @PostMapping("/chat/compare")
    public ApiResponse<?> compare(@RequestBody List<FinancialProductGptRequest> products) {
        StringBuilder sb = new StringBuilder();
        for (FinancialProductGptRequest product : products) {
            sb.append(product.toString()).append("\n\n");
        }
        sb.append(comparePrompt);
        System.out.println("====================");
        System.out.println(sb.toString());
        System.out.println("====================");
        String response = chatGptService.compare(List.of(new ChatMessage(sb.toString())));
        return ApiResponse.success(ResponseCode.CHATGPT_COMPARE_SUCCESS, response);
    }

}
