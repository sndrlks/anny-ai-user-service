package com.annyai.common.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
    
    /**
     * Redis script for atomic rotation of refresh tokens and per-user mapping.
     */
    @Bean
    public org.springframework.data.redis.core.script.RedisScript<String> rotateRefreshTokenScript() {
        return new org.springframework.data.redis.core.script.DefaultRedisScript<>("rotate-refresh-token.lua");
    }
}