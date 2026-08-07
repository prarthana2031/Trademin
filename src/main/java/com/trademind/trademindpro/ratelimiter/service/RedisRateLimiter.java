package com.trademind.trademindpro.ratelimiter.service;

import java.util.Collections;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import com.trademind.trademindpro.ratelimiter.config.RateLimitProperties;

@Service
public class RedisRateLimiter {

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> rateLimiterScript;
    private final RateLimitProperties properties;

   

    public RedisRateLimiter(
        StringRedisTemplate redisTemplate,
        RateLimitProperties properties) {
            this.redisTemplate = redisTemplate;
            this.properties = properties;

      

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("fixedWindow.lua"));
        script.setResultType(Long.class);

        this.rateLimiterScript = script;
    }

    public boolean allowRequest(
            String clientId,
            String ip,
            String endpoint) {

        String clientKey = "client:" + clientId;
        String ipKey = "ip:" + ip;
        String endpointKey = "endpoint:" + endpoint;

        if (!checkClientLimit(clientKey)) {
            return false;
        }

        if (!checkIpLimit(ipKey)) {
            return false;
        }

        if (!checkEndpointLimit(endpointKey)) {
            return false;
        }

        return true;
    }

    private boolean checkClientLimit(String clientKey) {

        Long result = redisTemplate.execute(
                rateLimiterScript,
                Collections.singletonList(clientKey),
                String.valueOf(properties.getClient().getWindow()),
                String.valueOf(properties.getClient().getLimit()));

        return result != null && result != -1;
    }

    private boolean checkIpLimit(String ipKey) {

        Long result = redisTemplate.execute(
                rateLimiterScript,
                Collections.singletonList(ipKey),
                String.valueOf(properties.getIp().getWindow()),
                String.valueOf(properties.getIp().getLimit()));

        return result != null && result != -1;
    }

    private boolean checkEndpointLimit(String endpointKey) {

        Long result = redisTemplate.execute(
                rateLimiterScript,
                Collections.singletonList(endpointKey),
                String.valueOf(properties.getEndpoint().getWindow()),
                String.valueOf(properties.getEndpoint().getLimit()));

        return result != null && result != -1;
    }
}