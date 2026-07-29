package com.trademind.trademindpro.ratelimiter.controller;

import com.trademind.trademindpro.ratelimiter.service.RedisRateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rate-limit")
public class RateLimiterController {

    private final RedisRateLimiter redisRateLimiter;

    public RateLimiterController(RedisRateLimiter redisRateLimiter) {
        this.redisRateLimiter = redisRateLimiter;
    }

    @PostMapping("/{clientId}")
    public ResponseEntity<String> allowRequest(@PathVariable String clientId) {

        boolean allowed = redisRateLimiter.allowRequest(clientId);

        if (allowed) {
            return ResponseEntity.ok("Request Allowed");
        }

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body("Rate Limit Exceeded");
    }
}
