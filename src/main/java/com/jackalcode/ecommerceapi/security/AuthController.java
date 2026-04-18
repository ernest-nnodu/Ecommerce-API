package com.jackalcode.ecommerceapi.security;

import com.jackalcode.ecommerceapi.security.jwt.JwtConfig;
import com.jackalcode.ecommerceapi.customer.CustomerResponse;
import com.jackalcode.ecommerceapi.dtos.responses.JwtResponse;
import com.jackalcode.ecommerceapi.security.jwt.JwtService;
import com.jackalcode.ecommerceapi.customer.CustomerRepository;
import com.jackalcode.ecommerceapi.customer.CustomerService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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

        var customer = customerRepository.findByEmail(loginRequest.email()).orElseThrow();

        //Generate access and refresh token
        var accessToken = jwtService.generateAccessToken(customer);
        var refreshToken = jwtService.generateRefreshToken(customer);

        //Add refresh token to http only cookie
        var cookie = new Cookie("refreshToken", refreshToken.toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(Math.toIntExact(jwtConfig.getRefreshTokenExpiration())); //7 day expiration
        cookie.setSecure(true);

        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(
            @CookieValue(value = "refreshToken") String refreshToken) {

        //Validate refresh token
        var jwt = jwtService.parseToken(refreshToken);
        if (jwt == null || jwt.isExpired()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        //Generate new access token if customer exists
        var customer = customerRepository.findById(jwt.getCustomerId()).orElseThrow();
        var accessToken = jwtService.generateAccessToken(customer);

        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }

    @GetMapping("/current-user")
    public ResponseEntity<CustomerResponse> getCurrentUser() {

        var userId = (Long) Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        return ResponseEntity.ok(customerService.getCustomer());
    }
}
