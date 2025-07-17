package org.scoula.chatgpt.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.scoula.chatgpt.dto.gpt.ChatCompletionRequest;
import org.scoula.chatgpt.dto.gpt.ChatCompletionResponse;
import org.scoula.chatgpt.exception.ChatGptDeserializationException;
import org.scoula.chatgpt.exception.ChatGptJsonParsingException;
import org.scoula.chatgpt.exception.ChatGptRequestParsingException;
import org.scoula.chatgpt.exception.ChatGptRetrievalException;
import org.scoula.response.ResponseCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatGptUtil {
    private final HttpClient client = HttpClient.newBuilder().build();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${openai.url.base}")
    private String API_BASE_URL;

    @Value("${chatgpt.api-key}")
    private String API_KEY;

    public String createChatCompletion(ChatCompletionRequest requestBody, String API_URL) {
        String jsonBody = jsonParsing(requestBody);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + API_URL))
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Accept", "application/json; charset=UTF-8")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request,
                    BodyHandlers.ofString(StandardCharsets.UTF_8));
            System.out.println("OpenAI API response body: " + response.body());
            if (response.statusCode() != 200) {
                log.error("OpenAI 응답 실패: {}", response.body());
                throw new ChatGptRetrievalException(ResponseCode.CHATGPT_RETRIEVAL_FAILED);
            }
            return extractChatCompletionResponse(response.body());
        }  catch (Exception e) {
            log.error("ChatGPT API 호출 중 예외 발생", e);
            throw new ChatGptRetrievalException(ResponseCode.CHATGPT_RETRIEVAL_FAILED);
        }
    }
    public String jsonParsing(ChatCompletionRequest request) {
        try {
            return mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new ChatGptRequestParsingException(ResponseCode.CHATGPT_REQUEST_PARSING_FAILED);
        }
    }

    public String extractChatCompletionResponse(String response) {
        try {
            ChatCompletionResponse chatResponse = mapper.readValue(response, ChatCompletionResponse.class);
            return extractAnswer(chatResponse);
        } catch (Exception e) {
            throw new ChatGptDeserializationException(ResponseCode.CHATGPT_DESERIALIZATION_FAILED);
        }
    }

    public String extractAnswer(ChatCompletionResponse response) {
        try {
            return mapper.writeValueAsString(response.choices().get(0).getMessage().getContent());
        } catch (JsonProcessingException e) {
            throw new ChatGptJsonParsingException(ResponseCode.CHATGPT_JSON_PARSING_FAILED);
        }
    }
}
