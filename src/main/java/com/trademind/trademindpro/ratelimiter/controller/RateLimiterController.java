package com.trademind.trademindpro.ratelimiter.controller;

import com.trademind.trademindpro.ratelimiter.service.RedisRateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/rate-limit")
public class RateLimiterController {

    private final RedisRateLimiter redisRateLimiter;

    public RateLimiterController(RedisRateLimiter redisRateLimiter) {
        this.redisRateLimiter = redisRateLimiter;
    }

    @PostMapping("/{clientId}")
    public ResponseEntity<String> allowRequest(
        @PathVariable String clientId,
        HttpServletRequest request) {

        String ip = request.getRemoteAddr();

        String endpoint = request.getRequestURI();

        boolean allowed = redisRateLimiter.allowRequest(clientId, ip, endpoint);

        if (allowed) {
            return ResponseEntity.ok("Request Allowed");
        }

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body("Rate Limit Exceeded");
    }
}
