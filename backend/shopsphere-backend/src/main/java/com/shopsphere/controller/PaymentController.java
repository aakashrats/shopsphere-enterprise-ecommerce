package com.shopsphere.controller;

import com.shopsphere.dto.response.PaymentResponse;
import com.shopsphere.entity.User;
import com.shopsphere.repository.UserRepository;
import com.shopsphere.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;

    @PostMapping("/orders/{orderId}")
    public PaymentResponse initiatePayment(
            Authentication authentication,
            @PathVariable Long orderId) {

        User user = getAuthenticatedUser(authentication);

        return PaymentResponse.from(
                paymentService.initiatePayment(user.getId(), orderId)
        );
    }

    @PostMapping("/{paymentReference}/mock-result")
    public PaymentResponse processMockPayment(
            Authentication authentication,
            @PathVariable String paymentReference,
            @RequestParam boolean success) {

        User user = getAuthenticatedUser(authentication);

        return PaymentResponse.from(
                paymentService.processMockPayment(
                        user.getId(), paymentReference, success)
        );
    }

    @GetMapping("/{paymentReference}")
    public PaymentResponse getPaymentByReference(
            Authentication authentication,
            @PathVariable String paymentReference) {

        User user = getAuthenticatedUser(authentication);

        return PaymentResponse.from(
                paymentService.getPaymentByReference(
                        user.getId(), paymentReference)
        );
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("User is not authenticated");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Authenticated user not found"));
    }
}