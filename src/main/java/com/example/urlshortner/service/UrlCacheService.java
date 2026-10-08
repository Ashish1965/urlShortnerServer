package com.example.urlshortner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
public class UrlCacheService {

    private final StringRedisTemplate redisTemplate;

    private static final Logger log =
            LoggerFactory.getLogger(UrlCacheService.class);

    public UrlCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String key(String shortCode) {
        return "url:" + shortCode;
    }

    public String get(String shortCode) {

        String redisKey = key(shortCode);

        log.info("Fetching original URL from Redis: key={}", redisKey);

        String originalUrl =
                redisTemplate.opsForValue().get(redisKey);

        if (originalUrl == null) {
            log.info("Cache miss for key: {}", redisKey);
            return null;
        }

        log.info(
                "Cache hit: key={}, value={}",
                redisKey,
                originalUrl
        );

        return originalUrl;
    }

    public void put(
            String shortCode,
            String originalUrl,
            long ttlSeconds) {

        String redisKey = key(shortCode);

        log.info(
                "Caching URL: key={}, value={}, TTL={} seconds",
                redisKey,
                originalUrl,
                ttlSeconds
        );

        redisTemplate.opsForValue().set(
                redisKey,
                originalUrl,
                Duration.ofSeconds(ttlSeconds)
        );
    }

    public void delete(String shortCode) {

        String redisKey = key(shortCode);

        log.info("Deleting cache key: {}", redisKey);

        redisTemplate.delete(redisKey);
    }
}