package com.shopsphere.service;

import com.shopsphere.dto.request.LoginRequest;
import com.shopsphere.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    LoginResponse refreshAccessToken(String refreshToken);

    void logout(String refreshToken);
}