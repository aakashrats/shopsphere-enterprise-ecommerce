package com.shopsphere.repository;

import com.shopsphere.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllByOrderIdOrderByCreatedAtDesc(Long orderId);

   

    Optional<Payment> findByPaymentReference(String paymentReference);
}