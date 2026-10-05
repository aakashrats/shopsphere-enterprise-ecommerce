package com.shopsphere.controller;

import com.shopsphere.dto.request.LoginRequest;
import com.shopsphere.dto.request.RefreshTokenRequest;
import com.shopsphere.dto.response.LoginResponse;
import com.shopsphere.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshAccessToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(
                authService.refreshAccessToken(request.getRefreshToken())
        );


    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody RefreshTokenRequest request) {

        authService.logout(request.getRefreshToken());

        return ResponseEntity.noContent().build();
    }
}