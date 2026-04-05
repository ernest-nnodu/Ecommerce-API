package com.jackalcode.ecommerceapi.jwt;

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

    private final CustomerRepository customerRepository;

    @Value("${spring.jwt.secret}")
    private String secret;

    public String generateToken(Customer user) {

        final long expiration = 864_000_000;  //1 day expiration

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("name", user.getFirstName() + " " + user.getLastName())
                .claim("email", user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
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
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
