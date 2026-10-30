
package com.shopsphere.service.impl;

import com.shopsphere.entity.Order;
import com.shopsphere.entity.Payment;
import com.shopsphere.repository.OrderRepository;
import com.shopsphere.repository.PaymentRepository;
import com.shopsphere.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public Payment initiatePayment(Long userId, Long orderId) {
        validatePositiveId(userId, "User ID");
        validatePositiveId(orderId, "Order ID");

        Order order = orderRepository.findById(orderId)
                .filter(existingOrder ->
                        existingOrder.getUser().getId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found with ID: " + orderId
                ));

        if (!"PLACED".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalArgumentException(
                    "Payment can only be initiated for PLACED orders"
            );
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentReference(
                "PAY-" + UUID.randomUUID().toString()
                        .replace("-", "").substring(0, 20).toUpperCase()
        );
        payment.setPaymentMethod("MOCK");
        payment.setStatus("PENDING");
        payment.setAmount(order.getTotalAmount());

        return paymentRepository.save(payment);
    }

    @Override
    public Payment processMockPayment(
            Long userId,
            String paymentReference,
            boolean success) {

        validatePositiveId(userId, "User ID");

        if (paymentReference == null || paymentReference.isBlank()) {
            throw new IllegalArgumentException(
                    "Payment reference is required"
            );
        }

        Payment payment = findPaymentForUser(userId, paymentReference);

        if (!"PENDING".equalsIgnoreCase(payment.getStatus())) {
            throw new IllegalArgumentException(
                    "Only PENDING payments can be processed"
            );
        }

        if (!"PLACED".equalsIgnoreCase(payment.getOrder().getStatus())) {
            throw new IllegalArgumentException(
                    "Payment cannot be processed for a non-PLACED order"
            );
        }

        payment.setStatus(success ? "SUCCESS" : "FAILED");

        return paymentRepository.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentByReference(
            Long userId,
            String paymentReference) {

        validatePositiveId(userId, "User ID");

        if (paymentReference == null || paymentReference.isBlank()) {
            throw new IllegalArgumentException(
                    "Payment reference is required"
            );
        }

        return findPaymentForUser(userId, paymentReference);
    }

    private Payment findPaymentForUser(
            Long userId,
            String paymentReference) {

        return paymentRepository.findByPaymentReference(paymentReference)
                .filter(payment ->
                        payment.getOrder().getUser().getId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Payment not found with reference: " + paymentReference
                ));
    }

    private void validatePositiveId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " must be positive"
            );
        }
    }
}
