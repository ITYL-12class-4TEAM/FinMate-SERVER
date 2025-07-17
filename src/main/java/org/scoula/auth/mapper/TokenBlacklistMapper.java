package org.scoula.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.auth.domain.TokenBlacklist;

@Mapper
public interface TokenBlacklistMapper {
    void insertBlacklistToken(TokenBlacklist token); //
    boolean isTokenBlacklisted(@Param("token") String token); //
}
