package com.trademind.trademindpro.ratelimiter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rate-limit")
public class RateLimiterController {

    @PostMapping("/{clientId}")
    public ResponseEntity<String> allowRequest(
            @PathVariable String clientId) {

        return ResponseEntity.ok(
                "Request Allowed for client: " + clientId);
    }
}