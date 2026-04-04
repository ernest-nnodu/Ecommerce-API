package com.jackalcode.ecommerceapi.services.impl;

import com.jackalcode.ecommerceapi.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@AllArgsConstructor
public class AuthenticationService implements UserDetailsService {

    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        var user = customerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        return new User(user.getEmail(), user.getPassword(), Collections.emptyList());
    }
}
