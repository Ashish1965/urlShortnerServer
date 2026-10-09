package com.example.urlshortner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.Set;

@Service
public class UrlCacheService {

    private final StringRedisTemplate redisTemplate;

    private static final Logger log = LoggerFactory.getLogger(UrlCacheService.class);

    public UrlCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String key(String shortCode) {
        return "url:" + shortCode;
    }

    public Long incrementClickCount(String shortCode) {

        String luaScript = """
                local count = redis.call('INCR', KEYS[1])
                redis.call('SADD', KEYS[2], ARGV[1])
                return count
                """;

        DefaultRedisScript<Long> script = new DefaultRedisScript<>(luaScript, Long.class);

        return redisTemplate.execute(
                script,
                List.of(
                        "clicks:" + shortCode,
                        "click-sync:shortcodes"),
                shortCode);
    }

    public String get(String shortCode) {

        String redisKey = key(shortCode);

        log.info("Fetching original URL from Redis: key={}", redisKey);

        String originalUrl = redisTemplate.opsForValue().get(redisKey);

        if (originalUrl == null) {
            log.info("Cache miss for key: {}", redisKey);
            return null;
        }

        log.info(
                "Cache hit: key={}, value={}",
                redisKey,
                originalUrl);

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
                ttlSeconds);

        redisTemplate.opsForValue().set(
                redisKey,
                originalUrl,
                Duration.ofSeconds(ttlSeconds));
    }

    public void delete(String shortCode) {

        String redisKey = key(shortCode);

        log.info("Deleting cache key: {}", redisKey);

        redisTemplate.delete(redisKey);
    }

    public String claimPendingBatch(String shortCode) {

        String batchId = UUID.randomUUID().toString();

        String luaScript = """
                local count = redis.call('GET', KEYS[1])

                if not count or tonumber(count) <= 0 then
                    redis.call('SREM', KEYS[4], ARGV[2])
                    return 0
                end

                redis.call('HSET', KEYS[2],
                    'batchId', ARGV[1],
                    'shortCode', ARGV[2],
                    'clickCount', count)

                redis.call('SADD', KEYS[3], ARGV[1])
                redis.call('DEL', KEYS[1])
                redis.call('SREM', KEYS[4], ARGV[2])

                return tonumber(count)
                """;

        DefaultRedisScript<Long> script = new DefaultRedisScript<>(luaScript, Long.class);

        Long count = redisTemplate.execute(
                script,
                List.of(
                        "clicks:" + shortCode,
                        "click-sync:batch:" + batchId,
                        "click-sync:batches",
                        "click-sync:shortcodes"),
                batchId,
                shortCode);

        if (count == null || count <= 0) {
            return null;
        }

        log.info(
                "Claimed batch {} for {} with {} clicks",
                batchId,
                shortCode,
                count);

        return batchId;
    }

    public Set<String> getPendingShortCodes() {

        Set<String> shortCodes = redisTemplate.opsForSet()
                .members("click-sync:shortcodes");

        return shortCodes == null ? Set.of() : shortCodes;
    }
}