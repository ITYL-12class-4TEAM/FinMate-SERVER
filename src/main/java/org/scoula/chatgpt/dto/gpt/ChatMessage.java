package org.scoula.chatgpt.dto.gpt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ChatMessage {

    private String role;
    private String content;
    private String refusal;  // 추가
    private List<Object> annotations;  // 추가

    public ChatMessage() {
    }

    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public ChatMessage(String content) {
        this.role = "user";
        this.content = content;
    }
}
