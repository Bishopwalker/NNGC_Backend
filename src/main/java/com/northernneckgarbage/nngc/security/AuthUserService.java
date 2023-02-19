package com.northernneckgarbage.nngc.security;


import com.northernneckgarbage.nngc.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthUserService implements UserDetailsService {

private final CustomerRepository customerRepository;
private final String USER_NOT_FOUND_MSG = "User with email %s not found";
        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return customerRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, username)));
        }
}
