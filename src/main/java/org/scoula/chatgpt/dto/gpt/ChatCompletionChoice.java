package org.scoula.chatgpt.dto.gpt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class ChatCompletionChoice {

    private Integer index;

    @JsonProperty("message")
    private ChatMessage message; // 필드명 정확히 맞추기, @JsonProperty 권장

    @JsonProperty("finish_reason")
    private String finishReason;
}
