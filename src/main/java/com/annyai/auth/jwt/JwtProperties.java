package com.annyai.auth.jwt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(prefix = "security.jwt")
@Validated
public record JwtProperties(@NotBlank String secretKey,
                            @Positive Duration expiration) {
}