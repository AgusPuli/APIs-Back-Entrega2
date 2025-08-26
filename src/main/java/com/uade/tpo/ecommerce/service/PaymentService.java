package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Payment;
import com.uade.tpo.ecommerce.entity.dto.PaymentRequest;

public interface PaymentService {
    public Payment pay(PaymentRequest request);
}