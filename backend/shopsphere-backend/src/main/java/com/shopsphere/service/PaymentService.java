
package com.shopsphere.service;

import com.shopsphere.entity.Payment;

public interface PaymentService {

    Payment initiatePayment(Long userId, Long orderId);

    Payment processMockPayment(Long userId, String paymentReference, boolean success);

    Payment getPaymentByReference(Long userId, String paymentReference);
}
