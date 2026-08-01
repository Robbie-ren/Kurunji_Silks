package com.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http

                // =====================================================
                // CORS
                // =====================================================

                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // =====================================================
                // Disable CSRF (JWT Authentication)
                // =====================================================

                .csrf(AbstractHttpConfigurer::disable)

                // =====================================================
                // Stateless Session
                // =====================================================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // =====================================================
                // Authorization Rules
                // =====================================================

                .authorizeHttpRequests(auth -> auth

                        // -----------------------------------------------
                        // Allow Browser CORS Preflight
                        // -----------------------------------------------

                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()

                        // -----------------------------------------------
                        // Authentication APIs
                        // -----------------------------------------------

                        .requestMatchers("/api/auth/**")
                        .permitAll()

                        // -----------------------------------------------
                        // Swagger
                        // -----------------------------------------------

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // -----------------------------------------------
                        // Spring Error Endpoint
                        // -----------------------------------------------

                        .requestMatchers("/error")
                        .permitAll()

                        // -----------------------------------------------
                        // PUBLIC PRODUCT BROWSING
                        // -----------------------------------------------

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/products/**",
                                "/api/categories/**",
                                "/api/images/**"
                        ).permitAll()

                        // -----------------------------------------------
                        // PRODUCT / CATEGORY / IMAGE MANAGEMENT
                        // ADMIN ONLY
                        // -----------------------------------------------

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/products/**",
                                "/api/categories/**",
                                "/api/images/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/products/**",
                                "/api/categories/**",
                                "/api/images/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/products/**",
                                "/api/categories/**",
                                "/api/images/**"
                        ).hasRole("ADMIN")

                        // -----------------------------------------------
                        // ADMIN MODULE
                        // -----------------------------------------------

                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // -----------------------------------------------
                        // CUSTOMER MODULE
                        // -----------------------------------------------

                        .requestMatchers(
                                "/api/cart/**",
                                "/api/orders/**"
                        ).hasAnyRole("CUSTOMER", "ADMIN")

                        // -----------------------------------------------
                        // EVERYTHING ELSE
                        // -----------------------------------------------

                        .anyRequest()
                        .authenticated()
                )

                // =====================================================
                // JWT Filter
                // =====================================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }
}