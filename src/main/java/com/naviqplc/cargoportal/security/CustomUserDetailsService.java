package com.naviqplc.cargoportal.security;

import com.naviqplc.cargoportal.entity.UserLogin;
import com.naviqplc.cargoportal.repository.UserLoginRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserLoginRepository userLoginRepository;

    public CustomUserDetailsService(UserLoginRepository userLoginRepository) {
        this.userLoginRepository = userLoginRepository;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. Find the row in user_login
        Optional<UserLogin> result = userLoginRepository.findByUsername(username);
        if (result.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        UserLogin user = result.get();

        // 2. Give Spring Security the details it needs
        return User.withUsername(user.getUsername())
                .password(user.getPasswordHash())        // the BCrypt hash
                .roles(user.getRole().name())            // BUSINESS_ADMIN or AGENT_USER
                .disabled(!user.getActive())             // is_active = false → blocked
                .build();
    }
}
