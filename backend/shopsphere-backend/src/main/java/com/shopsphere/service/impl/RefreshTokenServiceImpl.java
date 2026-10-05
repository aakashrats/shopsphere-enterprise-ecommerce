package com.shopsphere.service.impl;

import com.shopsphere.entity.RefreshToken;
import com.shopsphere.entity.User;
import com.shopsphere.repository.RefreshTokenRepository;
import com.shopsphere.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.shopsphere.exception.RefreshTokenException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshTokenExpiration;

    @Override
    public RefreshToken createRefreshToken(User user) {

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(
                        LocalDateTime.now()
                                .plusSeconds(refreshTokenExpiration / 1000)
                )
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() ->
                        new RefreshTokenException("Refresh token not found"));

        if (refreshToken.isRevoked()) {
            throw new RefreshTokenException("Refresh token has been revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RefreshTokenException("Refresh token has expired");
        }

        return refreshToken;
    }

    @Override
    public void revokeRefreshToken(String token) {

        RefreshToken refreshToken = verifyRefreshToken(token);

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }
}