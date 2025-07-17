package org.scoula.chatbot.session;

import java.util.ArrayList;
import java.util.List;
import org.scoula.chatgpt.dto.gpt.ChatMessage;

public class ChatSession {
    private String sessionId;
    private List<ChatMessage> messages = new ArrayList<>();

    public ChatSession(String sessionId) {
        this.sessionId = sessionId;
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }
}
