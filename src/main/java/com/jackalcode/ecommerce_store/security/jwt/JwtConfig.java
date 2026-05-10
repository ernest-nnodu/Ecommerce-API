package com.jackalcode.ecommerce_store.security.jwt;

import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
@ConfigurationProperties(prefix = "spring.jwt")
@Data
public class JwtConfig {

    private String secret;
    private Long accessTokenExpiration;
    private Long refreshTokenExpiration;

    public SecretKey getSecretKey() {

        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
