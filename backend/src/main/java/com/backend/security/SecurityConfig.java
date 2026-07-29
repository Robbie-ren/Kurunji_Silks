package com.backend.security;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    // ============================================================
    // SECURITY FILTER CHAIN
    // ============================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // ------------------------------------------------
                // Disable CSRF
                // ------------------------------------------------

                .csrf(csrf -> csrf.disable())


                // ------------------------------------------------
                // Do not create HTTP sessions
                // JWT is used instead
                // ------------------------------------------------

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                // ------------------------------------------------
                // Authorization rules
                // ------------------------------------------------

                .authorizeHttpRequests(auth -> auth

                        // ========================================
                        // AUTH ENDPOINTS
                        // ========================================

                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()


                        // ========================================
                        // SWAGGER
                        // ========================================

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()


                        // ========================================
                        // OTHER PUBLIC RESOURCES
                        // ========================================

                        .requestMatchers(
                                "/error"
                        ).permitAll()


                        // ========================================
                        // EVERYTHING ELSE
                        // ========================================

                        .anyRequest().authenticated()
                );


        // --------------------------------------------------------
        // Add JWT filter before UsernamePasswordAuthenticationFilter
        // --------------------------------------------------------

        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );


        return http.build();
    }


    // ============================================================
    // PASSWORD ENCODER
    // ============================================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // ============================================================
    // AUTHENTICATION MANAGER
    // ============================================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }
}