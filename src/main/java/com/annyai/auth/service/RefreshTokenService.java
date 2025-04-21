package com.annyai.auth.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import com.annyai.auth.config.RefreshTokenProperties;

import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    /**
     * Result of rotating a refresh token: original email and new token.
     */
    public record RotationResult(String email, String refreshToken) {}

    private final RedisTemplate<String, String> redisTemplate;
    private final RefreshTokenProperties properties;
    private final RedisScript<String> rotateScript;
    private final io.micrometer.core.instrument.Counter generateCounter;
    private final io.micrometer.core.instrument.Counter getHitCounter;
    private final io.micrometer.core.instrument.Counter getMissCounter;
    private final io.micrometer.core.instrument.Counter rotateCounter;
    private final io.micrometer.core.instrument.Counter revokeCounter;

    public RefreshTokenService(
            RedisTemplate<String, String> redisTemplate,
            com.annyai.auth.config.RefreshTokenProperties properties,
            org.springframework.data.redis.core.script.RedisScript<String> rotateRefreshTokenScript,
            io.micrometer.core.instrument.MeterRegistry meterRegistry) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
        this.rotateScript = rotateRefreshTokenScript;
        this.generateCounter = meterRegistry.counter("auth.refresh-token.generate");
        this.getHitCounter = meterRegistry.counter("auth.refresh-token.get", "result", "hit");
        this.getMissCounter = meterRegistry.counter("auth.refresh-token.get", "result", "miss");
        this.rotateCounter = meterRegistry.counter("auth.refresh-token.rotate");
        this.revokeCounter = meterRegistry.counter("auth.refresh-token.revoke");
    }

    /**
     * Generate a new refresh token for the given email, revoking any existing one.
     */
    public String generateRefreshToken(String email) {
        String token = UUID.randomUUID().toString();
        String key = properties.getPrefix() + token;
        String mappingKey = properties.getUserMappingPrefix() + email;
        // revoke any existing token for this user
        String oldKey = redisTemplate.opsForValue().get(mappingKey);
        if (oldKey != null) {
            redisTemplate.delete(oldKey);
        }
        // set new token and mapping
        redisTemplate.opsForValue().set(key, email, properties.getTtl());
        redisTemplate.opsForValue().set(mappingKey, key, properties.getTtl());
        generateCounter.increment();
        return token;
    }

    /**
     * Lookup email by refresh token, tracking hit/miss metrics.
     */
    public Optional<String> getEmailFromRefreshToken(String refreshToken) {
        String key = properties.getPrefix() + refreshToken;
        String email = redisTemplate.opsForValue().get(key);
        if (email != null) {
            getHitCounter.increment();
            return Optional.of(email);
        } else {
            getMissCounter.increment();
            return Optional.empty();
        }
    }

    /**
     * Atomically rotate a refresh token: generate a new one, revoke old, and update per-user mapping.
     */
    public Optional<RotationResult> rotateRefreshToken(String oldRefreshToken) {
        String oldKey = properties.getPrefix() + oldRefreshToken;
        // fetch email; if missing, invalid token
        String email = redisTemplate.opsForValue().get(oldKey);
        if (email == null) {
            getMissCounter.increment();
            return Optional.empty();
        }
        // prepare new token
        String newToken = UUID.randomUUID().toString();
        String newKey = properties.getPrefix() + newToken;
        String mappingKey = properties.getUserMappingPrefix() + email;
        String ttlSeconds = String.valueOf(properties.getTtl().getSeconds());
        // execute Redis Lua script for atomic rotate
        String result = redisTemplate.execute(
            rotateScript,
            java.util.List.of(oldKey, newKey, mappingKey),
            email, newToken, ttlSeconds
        );
        if (result == null) {
            getMissCounter.increment();
            return Optional.empty();
        }
        rotateCounter.increment();
        return Optional.of(new RotationResult(email, newToken));
    }

    /**
     * Revoke a refresh token and clear per-user mapping.
     */
    public void revokeRefreshToken(String refreshToken) {
        String key = properties.getPrefix() + refreshToken;
        // clear mapping if exists
        String email = redisTemplate.opsForValue().get(key);
        if (email != null) {
            String mappingKey = properties.getUserMappingPrefix() + email;
            redisTemplate.delete(mappingKey);
        }
        redisTemplate.delete(key);
        revokeCounter.increment();
    }
}