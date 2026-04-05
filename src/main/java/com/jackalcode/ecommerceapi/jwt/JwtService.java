package com.jackalcode.ecommerceapi.jwt;

import com.jackalcode.ecommerceapi.configs.JwtConfig;
import com.jackalcode.ecommerceapi.entities.Customer;
import com.jackalcode.ecommerceapi.repositories.CustomerRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;

    @Value("${spring.jwt.secret}")
    private String secret;

    public String generateAccessToken(Customer user) {

        return generateToken(user, jwtConfig.getAccessTokenExpiration());
    }

    public String generateRefreshToken(Customer user) {

        return generateToken(user, jwtConfig.getRefreshTokenExpiration());
    }

    public boolean validateToken(String token) {

        try {
            var claims = getClaims(token);

            return claims.getExpiration().after(new Date());
        } catch (JwtException e) {
            return false;
        }
    }

    public Long getSubject(String token) {

        return Long.valueOf(getClaims(token).getSubject());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String generateToken(Customer user, long expiration) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("name", user.getFirstName() + " " + user.getLastName())
                .claim("email", user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }
}
