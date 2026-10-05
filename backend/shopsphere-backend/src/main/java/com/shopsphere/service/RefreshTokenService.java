package com.shopsphere.service;

import com.shopsphere.entity.RefreshToken;
import com.shopsphere.entity.User;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user);

    RefreshToken verifyRefreshToken(String token);

    void revokeRefreshToken(String token);
}