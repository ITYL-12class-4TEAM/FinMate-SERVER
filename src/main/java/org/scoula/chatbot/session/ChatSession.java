package org.scoula.chatbot.session;

import java.util.ArrayList;
import java.util.List;
import org.scoula.chatgpt.dto.gpt.ChatMessage;

public class ChatSession {
    private String sessionId;
    private List<ChatMessage> messages = new ArrayList<>();
    private long lastAccessTime;

    public ChatSession(String sessionId) {
        this.sessionId = sessionId;
        this.messages = new ArrayList<>();
        this.lastAccessTime = System.currentTimeMillis();
    }

    public void updateLastAccessTime() {
        this.lastAccessTime = System.currentTimeMillis();
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        updateLastAccessTime();
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }
}
