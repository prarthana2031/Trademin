package com.trademind.trademindpro.ratelimiter.filter;

import com.trademind.trademindpro.ratelimiter.service.RedisRateLimiter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RateLimitFilter implements Filter {

    private final RedisRateLimiter redisRateLimiter;

    public RateLimitFilter(RedisRateLimiter redisRateLimiter) {
        this.redisRateLimiter = redisRateLimiter;
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest =
                (HttpServletRequest) request;

        HttpServletResponse httpResponse =
                (HttpServletResponse) response;

        String ip = httpRequest.getRemoteAddr();

        String endpoint = httpRequest.getRequestURI();

        String clientId = httpRequest.getHeader("X-Client-Id");

        if (clientId == null || clientId.isBlank()) {
            clientId = "anonymous";
        }

        boolean allowed = redisRateLimiter.allowRequest(
                clientId,
                ip,
                endpoint);

        if (!allowed) {

            httpResponse.setStatus(429);

            httpResponse.setHeader(
                    "Retry-After",
                    "60");

            httpResponse.setHeader(
                    "X-RateLimit-Limit",
                    "5");

            httpResponse.setHeader(
                    "X-RateLimit-Remaining",
                    "0");

            httpResponse.getWriter().write(
                    "Rate Limit Exceeded");

            return;
        }

        chain.doFilter(request, response);
    }
}