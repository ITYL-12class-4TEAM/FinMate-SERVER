package org.scoula.chatbot.session;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SessionCleanupScheduler {

    private final ChatSessionManager sessionManager;

    public SessionCleanupScheduler(ChatSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void cleanup() {
        sessionManager.cleanupExpiredSessions();
    }
}
