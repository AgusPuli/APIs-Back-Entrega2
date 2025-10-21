package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Payment;
import com.uade.tpo.ecommerce.controllers.payments.PaymentRequest;

import java.util.List;

public interface PaymentService {
    public Payment pay(PaymentRequest request);

    // 🔹 NUEVO: Método para obtener pagos por usuario
    public List<Payment> getPaymentsByUserId(Long userId);
}