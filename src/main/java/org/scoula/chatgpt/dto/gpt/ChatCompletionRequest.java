package org.scoula.chatgpt.dto.gpt;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatCompletionRequest {

    private String model;
    private List<ChatMessage> messages;

    @JsonProperty("max_tokens")
    private Integer maxTokens;

    public ChatCompletionRequest(String model, List<ChatMessage> messages, Integer maxTokens) {
        this.model = model;
        this.messages = messages;
        this.maxTokens = maxTokens;
    }
}