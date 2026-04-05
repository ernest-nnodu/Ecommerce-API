package com.jackalcode.ecommerceapi.controllers;

import com.jackalcode.ecommerceapi.dtos.requests.LoginRequest;
import com.jackalcode.ecommerceapi.dtos.responses.CustomerResponse;
import com.jackalcode.ecommerceapi.dtos.responses.JwtResponse;
import com.jackalcode.ecommerceapi.jwt.JwtService;
import com.jackalcode.ecommerceapi.repositories.CustomerRepository;
import com.jackalcode.ecommerceapi.services.CustomerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomerRepository customerRepository;
    private final CustomerService customerService;
    JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(), loginRequest.password()));

        var user = customerRepository.findByEmail(loginRequest.email()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);

        return ResponseEntity.ok(new JwtResponse(jwtToken));
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestHeader("Authorization") String token) {

        return ResponseEntity.ok(jwtService.validateToken(token.replace("Bearer ", "")));
    }

    @GetMapping("/current-user")
    public ResponseEntity<CustomerResponse> getCurrentUser() {

        var userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(customerService.getCustomerById(userId));
    }
}
