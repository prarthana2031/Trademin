package com.trademind.trademindpro.ratelimiter.service;

import java.util.Collections;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

@Service
public class RedisRateLimiter {

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> rateLimiterScript;

    private static final int LIMIT = 5;
    private static final int WINDOW_SECONDS = 60;

    public RedisRateLimiter(StringRedisTemplate redisTemplate) {

        this.redisTemplate = redisTemplate;

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("fixedWindow.lua"));
        script.setResultType(Long.class);

        this.rateLimiterScript = script;
    }

    public boolean allowRequest(String clientId) {

        String key = "rate_limit:" + clientId;

        Long result = redisTemplate.execute(
                rateLimiterScript,
                Collections.singletonList(key),
                String.valueOf(WINDOW_SECONDS),
                String.valueOf(LIMIT)
        );

        return result != null && result != -1;
    }
}