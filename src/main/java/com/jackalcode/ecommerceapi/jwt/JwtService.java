package com.jackalcode.ecommerceapi.jwt;

import com.jackalcode.ecommerceapi.configs.JwtConfig;
import com.jackalcode.ecommerceapi.entities.Customer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;

    public String generateAccessToken(Customer customer) {

        return generateToken(customer, jwtConfig.getAccessTokenExpiration());
    }

    public String generateRefreshToken(Customer customer) {

        return generateToken(customer, jwtConfig.getRefreshTokenExpiration());
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

    private String generateToken(Customer customer, long expiration) {
        return Jwts.builder()
                .subject(customer.getId().toString())
                .claim("name", customer.getFirstName() + " " + customer.getLastName())
                .claim("email", customer.getEmail())
                .claim("role", customer.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }
}
