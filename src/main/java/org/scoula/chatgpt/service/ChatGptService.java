package org.scoula.chatgpt.service;

import java.util.List;
import org.scoula.chatgpt.dto.gpt.ChatCompletionRequest;
import org.scoula.chatgpt.dto.gpt.ChatMessage;
import org.scoula.chatgpt.util.ChatGptUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChatGptService {

    @Value("${chatgpt.model}")
    private String model;

    @Value("${openai.url.prompt}")
    private String API_URL;

    @Value("${chatgpt.max-tokens}")
    private Integer maxTokens;

    private final ChatGptUtil chatGptUtil;

    public ChatGptService(ChatGptUtil chatGptUtil) {
        this.chatGptUtil = chatGptUtil;
    }

    public String summarize(List<ChatMessage> messages) {
        return chatGptUtil.createChatCompletion(toChatCompletionRequest(messages), API_URL);
    }

    public String compare(List<ChatMessage> messages) {
        return chatGptUtil.createChatCompletion(toChatCompletionRequest(messages), API_URL);
    }

    public ChatCompletionRequest toChatCompletionRequest(List<ChatMessage> messages) {
        return new ChatCompletionRequest(model, messages, maxTokens);
    }
}
