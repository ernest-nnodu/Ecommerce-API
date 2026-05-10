package com.jackalcode.ecommerce_store.security;

import com.jackalcode.ecommerce_store.customer.Customer;
import com.jackalcode.ecommerce_store.exceptions.CustomerNotFoundException;
import com.jackalcode.ecommerce_store.customer.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;

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

    public Customer getCurrentCustomer() {

        var customerId = (Long) Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        return customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException("Customer not found")
        );
    }
}
