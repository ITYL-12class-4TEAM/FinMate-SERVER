package org.scoula.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.auth.domain.UserSession;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SessionMapper {
    void insertSession(UserSession session);

    void updateLogoutTime(@Param("sessionId") Long sessionId,
                          @Param("logoutAt") LocalDateTime logoutAt);

    List<UserSession> findActiveSessionsByUserId(@Param("memberId") Long memberId);
}
