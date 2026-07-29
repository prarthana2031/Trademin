package com.trademind.trademindpro.ratelimiter.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisRateLimiter {

    private final StringRedisTemplate redisTemplate;

    private static final int LIMIT = 5;
    private static final int WINDOW_SECONDS = 60;

    public RedisRateLimiter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean allowRequest(String clientId) {

        String key = "rate_limit:" + clientId;

        String value = redisTemplate.opsForValue().get(key);

        if (value == null) {

            redisTemplate.opsForValue().set(
                    key,
                    "1",
                    WINDOW_SECONDS,
                    TimeUnit.SECONDS);

            return true;
        }

        int count = Integer.parseInt(value);

        if (count >= LIMIT) {
            return false;
        }

        redisTemplate.opsForValue().increment(key);

        return true;
    }
}