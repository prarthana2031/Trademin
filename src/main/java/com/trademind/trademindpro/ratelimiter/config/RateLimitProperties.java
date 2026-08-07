package com.trademind.trademindpro.ratelimiter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {

    private LimitRule client;
    private LimitRule ip;
    private LimitRule endpoint;

    public LimitRule getClient() {
        return client;
    }

    public void setClient(LimitRule client) {
        this.client = client;
    }

    public LimitRule getIp() {
        return ip;
    }

    public void setIp(LimitRule ip) {
        this.ip = ip;
    }
    
    public LimitRule getEndpoint() {
        return endpoint;
}

    public void setEndpoint(LimitRule endpoint) {
        this.endpoint = endpoint;
}
}