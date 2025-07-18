package org.scoula.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String ACCESS_PREFIX = "ACCESS:";

    // ✅ access token 저장 (30분 유효)
    public void saveAccessToken(String memberId, String token) {
        String key = ACCESS_PREFIX + memberId;
        redisTemplate.opsForValue().set(key, token, Duration.ofMinutes(30));

    }

    // access token 조회
    public String getAccessToken(String memberId) {
        return redisTemplate.opsForValue().get(ACCESS_PREFIX + memberId);
    }

    // access token 삭제
    public void deleteAccessToken(String memberId) {
        redisTemplate.delete(ACCESS_PREFIX + memberId);
    }

    // 💡 기타 일반 저장도 필요하면 유지 가능
    public void save(String key, String value) {
        redisTemplate.opsForValue().set(key, value); // TTL 없는 저장
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
