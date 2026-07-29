package com.backend.auth.service;

import com.backend.auth.dto.request.LoginRequest;
import com.backend.auth.dto.request.RegisterRequest;
import com.backend.auth.dto.response.LoginResponse;
import com.backend.auth.dto.response.UserResponse;
import com.backend.auth.entity.User;
import com.backend.auth.exception.EmailAlreadyExistsException;
import com.backend.auth.exception.InvalidCredentialsException;
import com.backend.auth.repository.UserRepository;
import com.backend.security.JwtService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;


    // ============================================================
    // REGISTER
    // ============================================================

    @Override
    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {

            throw new EmailAlreadyExistsException(
                    "An account already exists with email: "
                            + request.getEmail()
            );
        }

        User user = new User();

        user.setName(request.getName());

        user.setEmail(request.getEmail());

        user.setPhone(request.getPhone());

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setRole("CUSTOMER");

        user.setEnabled(true);

        User savedUser = userRepository.save(user);

        return convertToUserResponse(savedUser);
    }


    // ============================================================
    // LOGIN
    // ============================================================

    @Override
    public LoginResponse login(LoginRequest request) {

        Authentication authentication;

        try {

            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

        } catch (Exception exception) {

            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }


        // Get authenticated user details

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();


        // Find user from database

        User user = userRepository.findByEmail(
                request.getEmail()
        ).orElseThrow(() ->
                new InvalidCredentialsException(
                        "User account not found"
                )
        );


        // Generate JWT

        String token = jwtService.generateToken(
                userDetails,
                user.getRole()
        );


        // Convert user entity to response

        UserResponse userResponse =
                convertToUserResponse(user);


        // Create login response

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(userResponse)
                .build();
    }


    // ============================================================
    // USER ENTITY → USER RESPONSE
    // ============================================================

    private UserResponse convertToUserResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }
}