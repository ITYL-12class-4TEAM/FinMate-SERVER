package org.scoula.chatbot.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ChatSessionManager {
    private final Map<String, ChatSession> sessionStore = new ConcurrentHashMap<>();

    public ChatSession createSession(String sessionId) {
        ChatSession session = new ChatSession(sessionId);
        sessionStore.put(sessionId, session);
        return session;
    }

    public ChatSession getSession(String sessionId) {
        return sessionStore.get(sessionId);
    }

    public void removeSession(String sessionId) {
        sessionStore.remove(sessionId);
    }
}
