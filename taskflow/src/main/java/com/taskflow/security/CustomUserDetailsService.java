package com.taskflow.security;

import com.taskflow.entity.User;
import com.taskflow.repo.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // IMPORTANT:
        // user.getRole() should contain values like: "USER" or "ADMIN"
        // The builder will convert them to ROLE_USER / ROLE_ADMIN automatically.

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())  // email is username
                .password(user.getPassword())
                .roles(user.getRole())      // take role from DB without "ROLE_"
                .build();
    }
}

