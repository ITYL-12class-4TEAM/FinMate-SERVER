package org.scoula.chatgpt.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import org.scoula.chatgpt.dto.gpt.ChatMessage;
import org.scoula.chatgpt.dto.portfolio.ProductInfoRequest;
import org.scoula.chatgpt.dto.portfolio.ProductInfoResponse;
import org.scoula.chatgpt.dto.product.FinancialProductGptRequest;
import org.scoula.chatgpt.service.ChatGptService;
import org.scoula.chatgpt.service.ProductInfoService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Api(tags = "ChatGPT 금융 상품 API")
@RequestMapping("/api")
public class ChatGptController {

    private final ChatGptService chatGptService;
    private final ProductInfoService productInfoService;

    @Value("${chatgpt.summary-prompt}")
    private String summaryPrompt;

    @Value("${chatgpt.compare-prompt}")
    private String comparePrompt;

    public ChatGptController(ChatGptService chatGptService, ProductInfoService productInfoService) {
        this.chatGptService = chatGptService;
        this.productInfoService = productInfoService;
    }
    @ApiOperation(value = "금융 상품 요약 요청",
            notes = "금융 상품 정보를 입력받아, 사회초년생 및 금융 초보자를 위한 쉬운 요약을 반환합니다.")
    @PostMapping("/chat/summary")
    public ApiResponse<String> summarize(@RequestBody FinancialProductGptRequest product) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String productJson = mapper.writeValueAsString(product);
            String content = productJson + "\n\n" + summaryPrompt;
            ChatMessage message = new ChatMessage("user", content);
            String response = chatGptService.summarize(List.of(message));
            return ApiResponse.success(ResponseCode.CHATGPT_SUMMARY_SUCCESS, response);
        } catch (JsonProcessingException e) {
            return ApiResponse.fail(ResponseCode.CHATGPT_JSON_PARSING_FAILED);
        }
    }

    @ApiOperation(value = "금융 상품 비교 요청",
            notes = "복수의 금융 상품 정보를 입력받아, 각각의 상품을 비교하고 어떤 사람에게 더 적합한지 설명합니다.")
    @PostMapping("/chat/compare")
    public ApiResponse<String> compare(@RequestBody List<FinancialProductGptRequest> products) {
        try {
            StringBuilder sb = new StringBuilder();
            int count = 1;
            for (FinancialProductGptRequest product : products) {
                sb.append("상품 ").append(count).append(":\n");
                sb.append("상품명: ").append(product.getFinPrdtNm()).append("\n");
                sb.append("금리: ").append(product.getMaxIntrRate()).append("%\n");
                sb.append("가입대상: ").append(product.getJoinMember()).append("\n");
                sb.append("가입방법: ").append(product.getJoinWay()).append("\n");
                sb.append("특이사항: ").append(product.getProductDetail() != null
                        ? product.getProductDetail().getEtcNote() : "없음").append("\n\n");
                count++;
            }

            List<ChatMessage> messages = List.of(
                    new ChatMessage("system", comparePrompt),
                    new ChatMessage("user", sb.toString())
            );

            String response = chatGptService.compare(messages);
            return ApiResponse.success(ResponseCode.CHATGPT_COMPARE_SUCCESS, response);
        } catch (Exception e) {
            return ApiResponse.fail(ResponseCode.CHATGPT_JSON_PARSING_FAILED);
        }
    }
    @ApiOperation(
            value = "상품 자동입력용 정보 조회",
            notes = "상품명/금융회사(+선택: 카테고리/세부카테고리)를 바탕으로 최신 정보를 찾아 구조화해 반환합니다."
    )
    @PostMapping("/chat/product-info")
    public ApiResponse<ProductInfoResponse> getProductInfo(@Valid @RequestBody ProductInfoRequest request) {
        try {
            ProductInfoResponse response = productInfoService.getProductInfo(request);
            return ApiResponse.success(ResponseCode.CHATGPT_PRODUCT_INFO_SUCCESS, response);
        } catch (Exception e) {
            return ApiResponse.fail(ResponseCode.CHATGPT_PRODUCT_INFO_FAILED);
        }
    }
}
