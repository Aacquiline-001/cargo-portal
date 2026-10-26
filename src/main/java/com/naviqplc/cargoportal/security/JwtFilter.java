package com.naviqplc.cargoportal.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private JwtUtil jwtUtil;
    private CustomUserDetailsService userDetailsService;

    public JwtFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Read the header:  Authorization: Bearer eyJhbGci...
        String header = request.getHeader("Authorization");

        // 2. Only continue if the header exists and starts with "Bearer "
        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);   // remove the first 7 characters "Bearer "

            // 3. Is the token real and not expired?
            if (jwtUtil.isTokenValid(token)) {
                try {
                    // 4. Who is this? Load them from user_login
                    String username = jwtUtil.getUsername(token);
                    UserDetails user = userDetailsService.loadUserByUsername(username);

                    // 5. Tell Spring Security: "this request is from this user"
                    if (user.isEnabled()) {
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                } catch (Exception e) {
                    // user was deleted after login → treat as not logged in
                }
            }
        }

        // 6. Continue to the next step (other filters, then the controller)
        filterChain.doFilter(request, response);
    }
}
