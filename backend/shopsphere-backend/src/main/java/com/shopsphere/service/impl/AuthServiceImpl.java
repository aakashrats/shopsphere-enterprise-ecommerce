package com.shopsphere.service.impl;

import com.shopsphere.dto.request.LoginRequest;
import com.shopsphere.dto.response.LoginResponse;
import com.shopsphere.entity.RefreshToken;
import com.shopsphere.entity.User;
import com.shopsphere.repository.UserRepository;
import com.shopsphere.service.AuthService;
import com.shopsphere.service.JwtService;
import com.shopsphere.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;


    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String accessToken = jwtService.generateToken(user.getEmail());

        RefreshToken refreshToken =
                 refreshTokenService.createRefreshToken(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .build();
    }

    @Override
    public LoginResponse refreshAccessToken(String refreshToken) {

        RefreshToken token = refreshTokenService.verifyRefreshToken(refreshToken);

        String accessToken =
                jwtService.generateToken(token.getUser().getEmail());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(token.getToken())
                .tokenType("Bearer")
                .build();
    }

    @Override
    public void logout(String refreshToken) {
        refreshTokenService.revokeRefreshToken(refreshToken);
    }
}