package com.shopsphere.service.impl;

import com.shopsphere.dto.request.LoginRequest;
import com.shopsphere.dto.response.LoginResponse;
import com.shopsphere.entity.User;
import com.shopsphere.repository.UserRepository;
import com.shopsphere.service.AuthService;
import com.shopsphere.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String accessToken = jwtService.generateToken(user.getEmail());


        return LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .build();
    }
}