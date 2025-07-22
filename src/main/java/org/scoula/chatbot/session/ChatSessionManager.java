package org.scoula.chatbot.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ChatSessionManager {
    private Map<String, ChatSession> sessions = new ConcurrentHashMap<>();
    private final long sessionTimeoutMillis = 30 * 60 * 1000; // 30분
//    private final long sessionTimeoutMillis = 1 * 60 * 1000; // 1분

    public ChatSession createSession(String sessionId) {
        ChatSession session = new ChatSession(sessionId);
        sessions.put(sessionId, session);
        return session;
    }

    public ChatSession getSession(String sessionId) {
        ChatSession session = sessions.get(sessionId);
        if (session == null) return null;

        long now = System.currentTimeMillis();
        if (now - session.getLastAccessTime() > sessionTimeoutMillis) {
            sessions.remove(sessionId);
            return null;
        }
        return session;
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public void cleanupExpiredSessions() {
        long now = System.currentTimeMillis();
        sessions.entrySet().removeIf(entry -> now - entry.getValue().getLastAccessTime() > sessionTimeoutMillis);
    }
}
