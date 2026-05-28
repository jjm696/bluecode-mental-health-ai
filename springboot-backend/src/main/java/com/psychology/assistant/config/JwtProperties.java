package com.psychology.assistant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class JwtProperties {

    private String jwtSecret;
    private int jwtExpireDays;

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public int getJwtExpireDays() {
        return jwtExpireDays;
    }

    public void setJwtExpireDays(int jwtExpireDays) {
        this.jwtExpireDays = jwtExpireDays;
    }
}
