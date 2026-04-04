package com.jackalcode.ecommerceapi.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {


        http
                //Create stateless session (No need for session management, using token-based)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                //Disable cross site request forgery
                .csrf(AbstractHttpConfigurer::disable)

                //Allow public access to register customers, authenticate all other endpoints
                .authorizeHttpRequests((customAuthorizeRequests) ->
                        customAuthorizeRequests
                                .requestMatchers(HttpMethod.POST, "/customers").permitAll()
                                .anyRequest().authenticated());

        return http.build();
    }
}
