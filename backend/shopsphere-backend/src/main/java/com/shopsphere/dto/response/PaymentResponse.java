
package com.shopsphere.dto.response;

import com.shopsphere.entity.Payment;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentResponse {

    private Long id;
    private Long orderId;
    private String orderNumber;
    private String paymentReference;
    private String paymentMethod;
    private String status;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PaymentResponse from(Payment payment) {
        PaymentResponse response = new PaymentResponse();

        response.setId(payment.getId());
        response.setOrderId(payment.getOrder().getId());
        response.setOrderNumber(payment.getOrder().getOrderNumber());
        response.setPaymentReference(payment.getPaymentReference());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setStatus(payment.getStatus());
        response.setAmount(payment.getAmount());
        response.setCreatedAt(payment.getCreatedAt());
        response.setUpdatedAt(payment.getUpdatedAt());

        return response;
    }
}
