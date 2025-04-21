package com.annyai.auth.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app.auth.refresh-token")
public class RefreshTokenProperties {

    private String prefix = "rt:";

    private String userMappingPrefix = "rt:user:";

    private Duration ttl = Duration.ofDays(7);

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getUserMappingPrefix() {
        return userMappingPrefix;
    }

    public void setUserMappingPrefix(String userMappingPrefix) {
        this.userMappingPrefix = userMappingPrefix;
    }

    public Duration getTtl() {
        return ttl;
    }

    public void setTtl(Duration ttl) {
        this.ttl = ttl;
    }
}