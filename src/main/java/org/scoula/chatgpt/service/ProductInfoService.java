package org.scoula.chatgpt.service;

import org.scoula.chatgpt.dto.gpt.ChatMessage;
import org.scoula.chatgpt.dto.portfolio.ProductInfoRequest;
import org.scoula.chatgpt.dto.portfolio.ProductInfoResponse;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductInfoService {

    private final ChatGptService chatGptService;
    private final ProductInfoParser productInfoParser;

    public ProductInfoService(ChatGptService chatGptService, ProductInfoParser productInfoParser) {
        this.chatGptService = chatGptService;
        this.productInfoParser = productInfoParser;
    }

    @Value("${chatgpt.deposit.prompt}")
    private String depositPrompt;

    @Value("${chatgpt.savings.prompt}")
    private String savingsPrompt;

    @Value("${chatgpt.insurance.prompt}")
    private String insurancePrompt;

    @Value("${chatgpt.pension.prompt}")
    private String pensionPrompt;

    @Value("${chatgpt.stock.prompt}")
    private String stockPrompt;

    @Value("${chatgpt.etc.prompt}")
    private String etcPrompt;

    public ProductInfoResponse getProductInfo(ProductInfoRequest request) {
        try {
            // 입력 카테고리 있으면 매핑, 없으면 감지
            String mappedCategory = (request.getCategory() != null && !request.getCategory().isEmpty())
                    ? mapCategoryToInternal(request.getCategory())
                    : detectCategory(request.getProductName(), request.getCompanyName());

            if ("etc".equals(mappedCategory)) {
                // 비지원 카테고리는 예외로 위임(컨트롤러에서 실패 코드로 변환)
                throw new UnsupportedOperationException("Unsupported category: etc");
            }

            // 프롬프트 생성
            String prompt = buildPrompt(mappedCategory, request.getProductName(), request.getCompanyName());

            // 메시지 구성
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", "당신은 한국의 금융상품 전문가입니다. 웹에서 최신 정보를 검색하여 정확한 JSON 형태로 응답해주세요."));
            messages.add(new ChatMessage("user", prompt));

            // GPT 호출
            String gptResponse = chatGptService.getProductInfo(messages);

            // 파싱
            ProductInfoResponse productInfo = productInfoParser.parseResponse(gptResponse, mappedCategory);

            // 입력받은 카테고리/세부카테고리 보존
            if (request.getCategory() != null) productInfo.setCategory(request.getCategory());
            if (request.getSubcategory() != null) productInfo.setSubcategory(request.getSubcategory());

            return productInfo;

        } catch (UnsupportedOperationException e) {
            throw e; // 컨트롤러에서 적절한 에러 코드로 변환
        } catch (Exception e) {
            // 필요한 경우 커스텀 예외로 감싸기
            throw new RuntimeException("Failed to retrieve product info", e);
        }
    }

    /** 화면 카테고리 → 내부 카테고리 키 */
    private String mapCategoryToInternal(String userCategory) {
        if (userCategory == null || userCategory.isEmpty()) return null;
        switch (userCategory) {
            case "예금": return "deposit";
            case "적금": return "savings";
            case "보험": return "insurance";
            case "연금": return "pension";
            case "주식": return "stock";
            case "기타": return "etc";
            default: return "deposit";
        }
    }

    /** 상품명/회사명 기반 카테고리 감지 */
    private String detectCategory(String productName, String companyName) {
        String name = (productName == null ? "" : productName).toLowerCase();
        String company = (companyName == null ? "" : companyName).toLowerCase();

        if (name.contains("적금")) return "savings";
        if (name.contains("보험") || company.contains("생명") || company.contains("보험")) return "insurance";
        if (name.contains("연금")) return "pension";
        if (name.contains("주식") || company.contains("증권")) return "stock";
        return "deposit";
    }

    /** 카테고리별 프롬프트 구성 */
    private String buildPrompt(String category, String productName, String companyName) {
        String template;
        switch (category) {
            case "deposit": template = depositPrompt; break;
            case "savings": template = savingsPrompt; break;
            case "insurance": template = insurancePrompt; break;
            case "pension": template = pensionPrompt; break;
            case "stock": template = stockPrompt; break;
            case "etc": template = etcPrompt; break;
            default: template = depositPrompt;
        }
        return template
                .replace("{productName}", productName == null ? "" : productName)
                .replace("{companyName}", companyName == null ? "" : companyName);
    }
}
