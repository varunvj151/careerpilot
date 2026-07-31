package com.careerpilot.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 10;
    private static final long TIME_WINDOW_MS = 60000;

    private static class TokenBucket {
        int tokens;
        long lastRefillTime;

        TokenBucket(int tokens, long lastRefillTime) {
            this.tokens = tokens;
            this.lastRefillTime = lastRefillTime;
        }
    }

    private final ConcurrentHashMap<String, TokenBucket> userBuckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        
        // Only rate limit AI heavy endpoints
        if (path.startsWith("/api/analysis") || path.startsWith("/api/improvement") || path.startsWith("/api/roadmap")) {
            
            // Check if POST request (which triggers AI jobs)
            if ("POST".equalsIgnoreCase(request.getMethod())) {
                String userPrincipal = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : request.getRemoteAddr();
                
                if (!tryConsume(userPrincipal)) {
                    log.warn("Rate limit exceeded for user: {}", userPrincipal);
                    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    response.getWriter().write("Too many requests. Please try again later.");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private synchronized boolean tryConsume(String userId) {
        long now = Instant.now().toEpochMilli();
        TokenBucket bucket = userBuckets.computeIfAbsent(userId, k -> new TokenBucket(MAX_REQUESTS_PER_MINUTE, now));

        // Refill bucket if time window passed
        if (now - bucket.lastRefillTime > TIME_WINDOW_MS) {
            bucket.tokens = MAX_REQUESTS_PER_MINUTE;
            bucket.lastRefillTime = now;
        }

        if (bucket.tokens > 0) {
            bucket.tokens--;
            return true;
        }

        return false;
    }
}
