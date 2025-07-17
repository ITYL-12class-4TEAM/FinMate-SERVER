package org.scoula.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.auth.domain.UserSession;
import org.scoula.auth.mapper.SessionMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionMapper sessionMapper;

    public void createSession(UserSession session) {
        session.setCreatedAt(LocalDateTime.now());
        sessionMapper.insertSession(session);
    }

    public void logoutSession(Long sessionId) {
        sessionMapper.updateLogoutTime(sessionId, LocalDateTime.now());
    }

    public List<UserSession> getActiveSessions(Long memberId) {
        return sessionMapper.findActiveSessionsByUserId(memberId);
    }
}
