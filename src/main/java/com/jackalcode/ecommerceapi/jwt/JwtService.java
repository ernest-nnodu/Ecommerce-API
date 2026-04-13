package com.jackalcode.ecommerceapi.jwt;

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

    public Jwt generateAccessToken(Customer customer) {

        return generateToken(customer, jwtConfig.getAccessTokenExpiration());
    }

    public Jwt generateRefreshToken(Customer customer) {

        return generateToken(customer, jwtConfig.getRefreshTokenExpiration());
    }

    public Jwt parseToken(String token) {

        try {
            var claims = getClaims(token);
            return new Jwt(claims, jwtConfig.getSecretKey());
        } catch (JwtException e) {
            return null;
        }
    }

    private Claims getClaims(String token) {

        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Jwt generateToken(Customer customer, long expiration) {
        var claims =  Jwts.claims()
                .subject(customer.getId().toString())
                .add("name", customer.getFirstName() + " " + customer.getLastName())
                .add("email", customer.getEmail())
                .add("role", customer.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .build();

        return new Jwt(claims, jwtConfig.getSecretKey());
    }
}
