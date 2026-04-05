package com.jackalcode.ecommerceapi.controllers;

import com.jackalcode.ecommerceapi.configs.JwtConfig;
import com.jackalcode.ecommerceapi.dtos.requests.LoginRequest;
import com.jackalcode.ecommerceapi.dtos.responses.CustomerResponse;
import com.jackalcode.ecommerceapi.dtos.responses.JwtResponse;
import com.jackalcode.ecommerceapi.jwt.JwtService;
import com.jackalcode.ecommerceapi.repositories.CustomerRepository;
import com.jackalcode.ecommerceapi.services.CustomerService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping(path = "/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomerRepository customerRepository;
    private final CustomerService customerService;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(), loginRequest.password()));

        var user = customerRepository.findByEmail(loginRequest.email()).orElseThrow();

        //Generate access and refresh token
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        //Add refresh token to http only cookie
        var cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(Math.toIntExact(jwtConfig.getRefreshTokenExpiration())); //7 day expiration
        cookie.setSecure(true);

        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(accessToken));
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestHeader("Authorization") String token) {

        return ResponseEntity.ok(jwtService.validateToken(token.replace("Bearer ", "")));
    }

    @GetMapping("/current-user")
    public ResponseEntity<CustomerResponse> getCurrentUser() {

        var userId = (Long) Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        return ResponseEntity.ok(customerService.getCustomerById(userId));
    }
}
