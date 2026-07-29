package com.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // ============================================================
        // 1. GET AUTHORIZATION HEADER
        // ============================================================

        String authorizationHeader =
                request.getHeader("Authorization");

        System.out.println("========================================");
        System.out.println("REQUEST URL: " + request.getRequestURI());
        System.out.println("AUTHORIZATION HEADER: " + authorizationHeader);


        // ============================================================
        // 2. CHECK BEARER TOKEN
        // ============================================================

        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            System.out.println("NO BEARER TOKEN FOUND");

            filterChain.doFilter(request, response);
            return;
        }


        // ============================================================
        // 3. EXTRACT JWT
        // ============================================================

        String jwtToken =
                authorizationHeader.substring(7);

        System.out.println("JWT TOKEN RECEIVED");


        // ============================================================
        // 4. EXTRACT USERNAME / EMAIL FROM TOKEN
        // ============================================================

        String username;

        try {

            username =
                    jwtService.extractUsername(jwtToken);

            System.out.println(
                    "USERNAME FROM JWT: " + username
            );

        } catch (Exception exception) {

            System.out.println(
                    "ERROR: COULD NOT EXTRACT USERNAME FROM JWT"
            );

            exception.printStackTrace();

            filterChain.doFilter(request, response);
            return;
        }


        // ============================================================
        // 5. CHECK WHETHER USER IS ALREADY AUTHENTICATED
        // ============================================================

        if (username != null &&
                SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null) {

            try {

                // ====================================================
                // 6. LOAD USER FROM DATABASE
                // ====================================================

                UserDetails userDetails =
                        customUserDetailsService
                                .loadUserByUsername(username);

                System.out.println(
                        "USER FOUND IN DATABASE: " +
                                userDetails.getUsername()
                );

                System.out.println(
                        "USER AUTHORITIES: " +
                                userDetails.getAuthorities()
                );


                // ====================================================
                // 7. VALIDATE TOKEN
                // ====================================================

                boolean tokenValid =
                        jwtService.isTokenValid(
                                jwtToken,
                                userDetails
                        );

                System.out.println(
                        "IS JWT VALID: " + tokenValid
                );


                // ====================================================
                // 8. CREATE AUTHENTICATION
                // ====================================================

                if (tokenValid) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );


                    // =================================================
                    // 9. STORE AUTHENTICATION IN SECURITY CONTEXT
                    // =================================================

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);

                    System.out.println(
                            "AUTHENTICATION SET SUCCESSFULLY"
                    );

                } else {

                    System.out.println(
                            "JWT TOKEN IS INVALID OR EXPIRED"
                    );
                }

            } catch (Exception exception) {

                System.out.println(
                        "ERROR WHILE AUTHENTICATING USER"
                );

                exception.printStackTrace();
            }
        }


        // ============================================================
        // 10. PRINT FINAL SECURITY CONTEXT
        // ============================================================

        System.out.println(
                "FINAL AUTHENTICATION: " +
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication()
        );

        System.out.println("========================================");


        // ============================================================
        // 11. CONTINUE REQUEST
        // ============================================================

        filterChain.doFilter(request, response);
    }
}