package com.cgv.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT Configuration Properties - bind từ application.yml [app.jwt.*]
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
public class JwtConfig {

    private String secret;
    private long expirationMs;
    private long refreshExpirationMs;
    private String issuer;
}
